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

import javafx.fxml.FXML;

import javafx.scene.control.ScrollPane;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author wlady
 */
public class ImagePaneController {
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
