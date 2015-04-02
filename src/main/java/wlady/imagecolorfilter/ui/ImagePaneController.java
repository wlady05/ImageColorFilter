/*
 * (c) 2015 wlady
 */

package wlady.imagecolorfilter.ui;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ScrollPane;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * FXML Controller class
 *
 * @author wlady
 */
public class ImagePaneController implements Initializable {
    @FXML
    private ScrollPane imageContainer;

    @FXML
    private ImageView imageView;

    /**
     * The original image.
     */
    private Image originalImage;

    /**
     * The altered image.
     */
    private Image transformedImage;

    public ImagePaneController() {
        // Empty
    }

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Empty
    }

    public Image getOriginalImage() {
        return originalImage;
    }

    public Image getTransformedImage() {
        return transformedImage;
    }

    public void setImage(Image image) {
        int width = 0;
        int height = 0;

        if (image != null) {
            width = (int) image.getWidth();
            height = (int) image.getHeight();
        }

        if (width > 0 && height > 0) {
            originalImage = image;
        } else {
            originalImage = null;
        }

        updateImage(originalImage);
    }

    /**
     * Updates the image view with altered image.
     *
     * @param image
     * is the altered image
     */
    public void updateImage(Image image) {
        transformedImage = image;

        imageView.setImage(transformedImage);
    }

    /**
     * Updates the image, based on sliders values.
     *
     * @param redPercent
     * @param greenPercent
     * @param bluePercent
     */
    public void updateImage(final double redPercent, final double greenPercent, final double bluePercent) {
        if (originalImage == null) {
            return ;
        }

        final int width = (int) originalImage.getWidth();
        final int height = (int) originalImage.getHeight();

        WritableImage bufferImage = new WritableImage(width, height);

        final PixelReader pixelReader = originalImage.getPixelReader();
        final PixelWriter pixelWriter = bufferImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);

                int r = (argb >> 16) & 0x00ff;
                int g = (argb >>  8) & 0x00ff;
                int b = (argb      ) & 0x00ff;

                r = Math.min((int) Math.round(r * redPercent), 255);
                g = Math.min((int) Math.round(g * greenPercent), 255);
                b = Math.min((int) Math.round(b * bluePercent), 255);

                argb = (argb & 0xff000000) | (r << 16) | (g << 8) | b;

                pixelWriter.setArgb(x, y, argb);
            }
        }

        updateImage(bufferImage);
    }

    public void scaleImage(boolean scale) {
        if (scale) {
            imageView.fitWidthProperty().bind(imageContainer.widthProperty());
            imageView.fitHeightProperty().bind(imageContainer.heightProperty());
        } else {
            imageView.fitWidthProperty().unbind();
            imageView.fitHeightProperty().unbind();

            imageView.setFitWidth(0);
            imageView.setFitHeight(0);
        }
    }
}