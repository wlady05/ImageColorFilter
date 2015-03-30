/*
 * (c) 2015 wlady
 */

package site.imagetransform;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author wlady
 */
public class SliderPaneController implements Initializable {
    private static final int BUCKET_COUNT = 255;

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

    public Label getName() {
        return componentName;
    }

    public Slider getSlider() {
        return componentSlider;
    }

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        componentValue.textProperty().bindBidirectional(componentSlider.valueProperty(), new NumberStringConverter("0.00"));

        series = new XYChart.Series<>();
        seriesData = series.getData();

        for (int i = 1; i <= BUCKET_COUNT; i++)  {
            String id = String.valueOf(i);

            seriesData.add(new XYChart.Data(id, 0));
        }

        histogramChart.getData().setAll(series);
    }

    public void updateHistogramChart(int[] histogram) {
        for (int i = 0; i < BUCKET_COUNT; i++)  {
            seriesData.get(i).setYValue(histogram[i]);
        }
    }
}
