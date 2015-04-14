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

import java.util.Arrays;

import java.util.function.Supplier;

import javafx.application.Platform;

import javafx.scene.image.Image;

import wlady.imagecolorfilter.TaskScheduler;
import wlady.imagecolorfilter.ImageColorFilter;
import wlady.imagecolorfilter.ImageColorHistogram;

/**
 * Used to group some common functionality.
 */
public class FilterProcessor {
    private final ImagePaneController imagePane;

    private final SliderPaneController[] sliderPanes;

    private Supplier<ImageColorFilter> colorFilterFactory;
    private Supplier<ImageColorHistogram> colorHistogramFactory;

    private TaskScheduler taskScheduler;

    public FilterProcessor(ImagePaneController imagePane, SliderPaneController... sliders) {
        this.imagePane = imagePane;
        this.sliderPanes = Arrays.copyOf(sliders, sliders.length);
    }

    public void setColorFilterFactory(Supplier<ImageColorFilter> colorFilterFactory) {
        this.colorFilterFactory = colorFilterFactory;
    }

    public void setColorHistogramFactory(Supplier<ImageColorHistogram> colorHistogramFactory) {
        this.colorHistogramFactory = colorHistogramFactory;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Initialize labels and charts.
     *
     * @param histogramDescriptions
     */
    public void init(ImageColorHistogram.Description[] histogramDescriptions) {
        for (int i = 0; i < histogramDescriptions.length; i++) {
            final SliderPaneController sliderPane = sliderPanes[i];

            sliderPane.getName().setText(histogramDescriptions[i].getName());
            sliderPane.prepareChart(histogramDescriptions[i].getBinCount());

            sliderPane.getSlider().valueProperty().addListener((observable, oldValue, newValue) -> updateImageLater());
        }
    }

    /**
     * Debounce calls to updateImage() method.
     */
    public void updateImageLater() {
        taskScheduler.cancel();
        taskScheduler.schedule(this::updateImage);
    }

    /**
     * Updates image and histogram charts.
     */
    public void updateImage() {
        double[] filter = new double[sliderPanes.length];

        for (int i = 0; i < filter.length; i++) {
            filter[i] = sliderPanes[i].getSlider().getValue() / 100.0;
        }

        Image image = imagePane.getOriginalImage();

        ImageColorFilter colorFilter;

        colorFilter = colorFilterFactory.get();
        colorFilter.filterImage(image, filter);

        ImageColorHistogram colorHistogram;

        colorHistogram = colorHistogramFactory.get();
        colorHistogram.calculateHistograms(colorFilter.getImage());

        Platform.runLater(() -> updateImage(colorFilter, colorHistogram));
    }

    /**
     * Updates image and histogram charts.
     *
     * <p>
     * This method should be called from JavaFx Application thread.
     *
     * @param colorFilter
     * is the color filter
     *
     * @param colorHistogram
     * is the color histogram
     */
    public void updateImage(ImageColorFilter colorFilter, ImageColorHistogram colorHistogram) {
        imagePane.setFilteredImage(colorFilter.getImage());

        for (int i = 0; i < sliderPanes.length; i++) {
            sliderPanes[i].updateHistogramChart(colorHistogram.getHistogram(i));
        }
    }
}
