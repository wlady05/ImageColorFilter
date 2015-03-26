/*
 * (c) 2015 wlady
 */

package site.imagetransform;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.util.converter.NumberStringConverter;

/**
 * Scene Controller class.
 *
 * @author wlady
 */
public class SceneController implements Initializable {
    @FXML
    private ImageView imageView;

    @FXML
    private Slider colorSliderRed;
    @FXML
    private Label colorValueRed;

    @FXML
    private Slider colorSliderGreen;
    @FXML
    private Label colorValueGreen;

    @FXML
    private Slider colorSliderBlue;
    @FXML
    private Label colorValueBlue;

    private Image originalImage;
    private WritableImage transformedImage;

    public SceneController() {
        // Empty
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        NumberStringConverter colorValueConverter = new NumberStringConverter("0.00");

        colorValueRed.textProperty().bindBidirectional(colorSliderRed.valueProperty(), colorValueConverter);
        colorValueGreen.textProperty().bindBidirectional(colorSliderGreen.valueProperty(), colorValueConverter);
        colorValueBlue.textProperty().bindBidirectional(colorSliderBlue.valueProperty(), colorValueConverter);

        colorSliderRed.valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImage());

        colorSliderGreen.valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImage());

        colorSliderBlue.valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateImage());

        setImage(new Image(getClass().getResourceAsStream("/image.jpg")));
    }

    public void setImage(Image image) {
        originalImage = image;

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        transformedImage = new WritableImage(originalImage.getPixelReader(), width, height);

        imageView.imageProperty().set(transformedImage);
    }    

    public void updateImage() {
        final double redPercent = colorSliderRed.getValue() / 100.0;
        final double greenPercent = colorSliderGreen.getValue() / 100.0;
        final double bluePercent = colorSliderBlue.getValue() / 100.0;

        final int width = (int) transformedImage.getWidth();
        final int height = (int) transformedImage.getHeight();

        final PixelReader pixelReader = originalImage.getPixelReader();
        final PixelWriter pixelWriter = transformedImage.getPixelWriter();

        for (int y= 0 ; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);

                int red = (argb >> 16) & 0x00ff;
                int green = (argb >> 8) & 0x00ff;
                int blue = argb & 0x00ff;

                red = (int) ((double) red * redPercent);
                green = (int) ((double) green * greenPercent);
                blue = (int) ((double) blue * bluePercent);

                argb = (argb & 0xff000000) | (red << 16) | (green << 8) | blue;

                pixelWriter.setArgb(x, y, argb);
            }
        }
    }
}
