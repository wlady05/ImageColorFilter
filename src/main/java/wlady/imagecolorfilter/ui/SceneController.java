/*
 * (c) 2015 wlady
 */

package wlady.imagecolorfilter.ui;

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

import javafx.stage.FileChooser;

import wlady.imagecolorfilter.ImageColorFilter;
import wlady.imagecolorfilter.ImageColorHistogram;

/**
 * Scene Controller class.
 *
 * @author wlady
 */
public class SceneController implements Initializable {
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
     * Used to schedule tasks for execution with some delay, so that the UI is more responsive.
     */
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Used to update image with some delay, so that the UI is more responsive.
     */
    private ScheduledFuture<?> updateImageTask = null;

    public SceneController() {
        // Empty
    }

    public void stop() {
        scheduledExecutorService.shutdownNow();
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
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImageLater());

        componentGreenController.getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImageLater());

        componentBlueController.getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImageLater());

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
        imagePaneController.setOriginalImage(image);

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
     * Debounce calls to updateImage() method.
     */
    public void updateImageLater() {
        if (updateImageTask != null && ! updateImageTask.isDone()) {
            updateImageTask.cancel(false);
        }

        updateImageTask = scheduledExecutorService.schedule(this::updateImage, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Updates the image and histogram charts.
     */
    public void updateImage() {
        Image originalImage = imagePaneController.getOriginalImage();

        double rFilter = componentRedController.getSlider().getValue() / 100.0;
        double gFilter = componentGreenController.getSlider().getValue() / 100.0;
        double bFilter = componentBlueController.getSlider().getValue() / 100.0;

        ImageColorFilter colorFilter;

        colorFilter = new ImageColorFilter(originalImage, rFilter, gFilter, bFilter);
        colorFilter.filterImage();

        ImageColorHistogram colorHistogram;

        colorHistogram = new ImageColorHistogram();
        colorHistogram.calculateColorHistograms(colorFilter.getImage());

        if (Platform.isFxApplicationThread()) {
            updateImageAndHistograms(colorFilter, colorHistogram);
        } else {
            Platform.runLater(() -> updateImageAndHistograms(colorFilter, colorHistogram));
        }
    }

    private void updateImageAndHistograms(ImageColorFilter colorFilter, ImageColorHistogram colorHistogram) {
        imagePaneController.setFilteredImage(colorFilter.getImage());

        componentRedController.updateHistogramChart(colorHistogram.getRed());
        componentGreenController.updateHistogramChart(colorHistogram.getGreen());
        componentBlueController.updateHistogramChart(colorHistogram.getBlue());
    }

    public void scaleImage() {
        imagePaneController.scaleImage(scaleImage.isSelected());
    }
}
