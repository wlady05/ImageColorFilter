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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Parent;

import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import javafx.scene.image.Image;

import javafx.stage.FileChooser;

import wlady.imagecolorfilter.TaskScheduler;

import wlady.imagecolorfilter.ImageColorFilterRgb;
import wlady.imagecolorfilter.ImageColorFilterHsb;

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
    private Accordion accordionPane;

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

    //
    // Application Data

    private final TaskScheduler taskScheduler;

    private final ObjectProperty<FilterProcessor> currentFilter;

    public SceneController() {
        taskScheduler = new TaskScheduler();

        currentFilter = new SimpleObjectProperty<>();
    }

    public void stop() {
        taskScheduler.stop();
    }

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FilterProcessor filter;

        filter = new FilterProcessor(imagePaneController, filterRedController, filterGreenController, filterBlueController);
        filter.setTaskScheduler(taskScheduler);
        filter.setColorFilterFactory(ImageColorFilterRgb::new);
        filter.setColorHistogramFactory(ImageColorHistogramRgb::new);
        filter.init(ImageColorHistogramRgb.HISTOGRAM_DESCRIPTION);

        paneRgb.setUserData(filter);

        filter = new FilterProcessor(imagePaneController, filterHueController, filterSaturationController, filterBrightnessController);
        filter.setTaskScheduler(taskScheduler);
        filter.setColorFilterFactory(ImageColorFilterHsb::new);
        filter.setColorHistogramFactory(ImageColorHistogramHsb::new);
        filter.init(ImageColorHistogramHsb.HISTOGRAM_DESCRIPTION);

        paneHsb.setUserData(filter);

        currentFilter.set((FilterProcessor) paneRgb.getUserData());
        currentFilter.addListener(
                (ObservableValue<? extends FilterProcessor> observable, FilterProcessor oldValue, FilterProcessor newValue) -> updateImage());

        scaleImage.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> scaleImage());

        accordionPane.expandedPaneProperty().addListener(
                (ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) -> changeCurrentFilter());

        setImage(new Image(getClass().getResourceAsStream("/image.jpg")));
    }

    public void changeCurrentFilter() {
        TitledPane titledPane = paneRgb;

        if (paneRgb.isExpanded()) {
            titledPane = paneRgb;
        } else if (paneHsb.isExpanded()) {
            titledPane = paneHsb;
        }

        currentFilter.set((FilterProcessor) titledPane.getUserData());
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

    public void updateImage() {
        FilterProcessor filter = currentFilter.get();

        if (filter == null) {
            imagePaneController.setFilteredImage(imagePaneController.getOriginalImage());
        } else {
            filter.updateImageLater();
        }
    }

    public void scaleImage() {
        imagePaneController.scaleImage(scaleImage.isSelected());
    }
}
