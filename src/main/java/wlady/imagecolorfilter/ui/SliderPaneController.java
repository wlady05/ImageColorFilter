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

import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class.
 *
 * @author wlady
 */
public class SliderPaneController {
    @FXML
    private BarChart<String,Number> histogramChart;

    @FXML
    private Label componentName;

    @FXML
    private Slider componentSlider;

    @FXML
    private Label componentValue;

    private XYChart.Series<String,Number> series;
    private ObservableList<XYChart.Data<String,Number>> seriesData;

    public SliderPaneController() {
        // Empty
    }

    public void prepareChart(int binCount) {
        series = new XYChart.Series<>();
        seriesData = series.getData();

        for (int i = 0; i < binCount; i++)  {
            String id = String.valueOf(i);

            seriesData.add(new XYChart.Data<>(id, 0));
        }

        histogramChart.getData().setAll(series);
    }

    public Label getName() {
        return componentName;
    }

    public Slider getSlider() {
        return componentSlider;
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        componentValue.textProperty().bindBidirectional(componentSlider.valueProperty(), new NumberStringConverter("0.00"));
    }

    public void updateHistogramChart(int[] histogram) {
        for (int i = 0; i < histogram.length; i++)  {
            seriesData.get(i).setYValue(histogram[i]);
        }
    }
}
