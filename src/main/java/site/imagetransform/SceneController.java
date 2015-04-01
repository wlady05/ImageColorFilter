/*
 * (c) 2015 wlady
 */

package site.imagetransform;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Parent;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import javafx.stage.FileChooser;

/**
 * Scene Controller class.
 *
 * @author wlady
 */
public class SceneController implements Initializable {
    private static final int BUCKET_COUNT = 255;

    @FXML
    private Parent mainView;

    @FXML
    private TextField imageWidth;

    @FXML
    private TextField imageHeight;

    @FXML
    private CheckBox scaleImage;

    @FXML
    private ImagePaneController imagePaneController;

    @FXML
    private SliderPaneController componentRedController;

    @FXML
    private SliderPaneController componentGreenController;

    @FXML
    private SliderPaneController componentBlueController;

    /**
     * Used to update histograms with some delay, so that the UI is more responsive.
     */
    private final ScheduledExecutorService updateHistogramsService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Used to update histograms with some delay, so that the UI is more responsive.
     */
    private ScheduledFuture<?> updateHistogramsTask = null;

    public SceneController() {
        // Empty
    }

    public void stop() {
        updateHistogramsService.shutdownNow();
    }

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        componentRedController.getName().setText("Red");
        componentGreenController.getName().setText("Green");
        componentBlueController.getName().setText("Blue");

        componentRedController.getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImage());

        componentGreenController.getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImage());

        componentBlueController.getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImage());

        scaleImage.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> scaleImage());

        setImage(new Image(getClass().getResourceAsStream("/image.jpg")));
    }

    @FXML
    private void openImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open Image");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.gif", "*.png"));

        File file = fileChooser.showOpenDialog(mainView.getScene().getWindow());

        if (file != null) {
            try {
                setImage(new Image(file.toURI().toURL().toString(), false));
            } catch (IOException ioe) {
                // Ignore this one ...
            }
        }
    }

    public void setImage(Image image) {
        imagePaneController.setImage(image);

        int w = 0;
        int h = 0;

        image = imagePaneController.getOriginalImage();

        if (image != null) {
            w = (int) image.getWidth();
            h = (int) image.getHeight();
        }

        imageWidth.setText(w + " px");
        imageHeight.setText(h + " px");

        updateImage();
    }

    /**
     * Updates the image and histogram charts, based on sliders values.
     */
    public void updateImage() {
        final double rPercent = componentRedController.getSlider().getValue() / 100.0;
        final double gPercent = componentGreenController.getSlider().getValue() / 100.0;
        final double bPercent = componentBlueController.getSlider().getValue() / 100.0;

        imagePaneController.updateImage(rPercent, gPercent, bPercent);

        updateHistogramsLater();
    }

    /**
     * Debounce calls to updateHistogram() method.
     */
    public void updateHistogramsLater() {
        if (updateHistogramsTask != null && ! updateHistogramsTask.isDone()) {
            updateHistogramsTask.cancel(false);
        }

        updateHistogramsTask = updateHistogramsService.schedule(() -> updateHistograms(), 150, TimeUnit.MILLISECONDS);
    }

    public void updateHistograms() {
        if (! Platform.isFxApplicationThread()) {
            Platform.runLater(() -> updateHistograms());
            return ;
        }

        int[] histogramRed = new int[BUCKET_COUNT];
        int[] histogramGreen = new int[BUCKET_COUNT];
        int[] histogramBlue = new int[BUCKET_COUNT];

        for (int i = 0; i < BUCKET_COUNT; i++) {
            histogramRed[i] = 0;
            histogramGreen[i] = 0;
            histogramBlue[i] = 0;
        }

        Image image = imagePaneController.getTransformedImage();

        if (image != null) {
            final PixelReader pixelReader = image.getPixelReader();

            final int width = (int) image.getWidth();
            final int height = (int) image.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = pixelReader.getArgb(x, y);

                    int r = (argb >> 16) & 0x00ff;
                    int g = (argb >>  8) & 0x00ff;
                    int b = (argb      ) & 0x00ff;

                    if (r > 0) {
                        histogramRed[r - 1]++;
                    }
                    if (g > 0) {
                        histogramGreen[g - 1]++;
                    }
                    if (b > 0) {
                        histogramBlue[b - 1]++;
                    }
                }
            }
        }

        componentRedController.updateHistogramChart(histogramRed);
        componentGreenController.updateHistogramChart(histogramGreen);
        componentBlueController.updateHistogramChart(histogramBlue);
    }

    public void scaleImage() {
        imagePaneController.scaleImage(scaleImage.isSelected());
    }
}
