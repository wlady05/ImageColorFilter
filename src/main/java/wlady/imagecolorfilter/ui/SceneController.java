/*
 * The MIT License
 *
 * Copyright 2015 wlady.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import javafx.scene.control.TitledPane;

import javafx.scene.image.Image;

import javafx.stage.FileChooser;

import wlady.imagecolorfilter.ImageColorFilter;
import wlady.imagecolorfilter.ImageColorFilterRgb;
import wlady.imagecolorfilter.ImageColorFilterHsb;

import wlady.imagecolorfilter.ImageColorHistogram;
import wlady.imagecolorfilter.ImageColorHistogramRgb;
import wlady.imagecolorfilter.ImageColorHistogramHsb;

/**
 * Scene Controller class.
 *
 * @author wlady
 */
public class SceneController implements Initializable {
    public static final String[] IMAGE_FILE_EXTENSIONS =
    {
        "*.jpg",
        "*.jpeg",
        "*.gif",
        "*.png"
    };

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

    //
    // RGB Filter

    @FXML
    private TitledPane paneRgb;

    @FXML
    private SliderPaneController filterRedController;

    @FXML
    private SliderPaneController filterGreenController;

    @FXML
    private SliderPaneController filterBlueController;

    //
    // HSB Filter

    @FXML
    private TitledPane paneHsb;

    @FXML
    private SliderPaneController filterHueController;

    @FXML
    private SliderPaneController filterSaturationController;

    @FXML
    private SliderPaneController filterBrightnessController;

    private SliderPaneController[] filterRgb;
    private SliderPaneController[] filterHsb;

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
        filterRgb = new SliderPaneController[3];
        filterHsb = new SliderPaneController[3];

        filterRgb[0] = filterRedController;
        filterRgb[1] = filterGreenController;
        filterRgb[2] = filterBlueController;

        for (int i = 0; i < ImageColorHistogramRgb.HISTOGRAM_DESCRIPTION.length; i++) {
            filterRgb[i].getName().setText(ImageColorHistogramRgb.HISTOGRAM_DESCRIPTION[i].getName());
            filterRgb[i].prepareChart(ImageColorHistogramRgb.HISTOGRAM_DESCRIPTION[i].getBinCount());

            filterRgb[i].getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImageLater());
        }

        filterHsb[0] = filterHueController;
        filterHsb[1] = filterSaturationController;
        filterHsb[2] = filterBrightnessController;

        for (int i = 0; i < ImageColorHistogramHsb.HISTOGRAM_DESCRIPTION.length; i++) {
            filterHsb[i].getName().setText(ImageColorHistogramHsb.HISTOGRAM_DESCRIPTION[i].getName());
            filterHsb[i].prepareChart(ImageColorHistogramHsb.HISTOGRAM_DESCRIPTION[i].getBinCount());

            filterHsb[i].getSlider().valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImageLater());
        }

        scaleImage.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> scaleImage());

        paneRgb.setExpanded(true);
        paneHsb.setExpanded(false);

        setImage(new Image(getClass().getResourceAsStream("/image.jpg")));
    }

    @FXML
    private void openImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open Image");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", IMAGE_FILE_EXTENSIONS));

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
        SliderPaneController[] filterPanes;

        ImageColorFilter colorFilter;
        ImageColorHistogram colorHistogram;

        if (paneHsb.isExpanded()) {
            filterPanes = filterHsb;

            colorFilter = new ImageColorFilterHsb();
            colorHistogram = new ImageColorHistogramHsb();
        } else {
            filterPanes = filterRgb;

            colorFilter = new ImageColorFilterRgb();
            colorHistogram = new ImageColorHistogramRgb();
        }

        double[] filter = new double[filterPanes.length];

        for (int i = 0; i < filter.length; i++) {
            filter[i] = filterPanes[i].getSlider().getValue() / 100.0;
        }

        Image originalImage = imagePaneController.getOriginalImage();

        colorFilter.filterImage(originalImage, filter);
        colorHistogram.calculateHistograms(colorFilter.getImage());

        if (Platform.isFxApplicationThread()) {
            updateImageAndHistograms(filterPanes, colorFilter, colorHistogram);
        } else {
            Platform.runLater(() -> updateImageAndHistograms(filterPanes, colorFilter, colorHistogram));
        }
    }

    private void updateImageAndHistograms(SliderPaneController[] filter, ImageColorFilter colorFilter, ImageColorHistogram colorHistogram) {
        imagePaneController.setFilteredImage(colorFilter.getImage());

        for (int i = 0; i < filter.length; i++) {
            filter[i].updateHistogramChart(colorHistogram.getHistogram(i));
        }
    }

    public void scaleImage() {
        imagePaneController.scaleImage(scaleImage.isSelected());
    }
}
