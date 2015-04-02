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
     * The filtered image.
     */
    private Image filteredImage;

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

    public Image getFilteredImage() {
        return filteredImage;
    }

    public void setOriginalImage(Image image) {
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

        setFilteredImage(originalImage);
    }

    public void setFilteredImage(Image image) {
        filteredImage = image;

        imageView.setImage(filteredImage);
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
