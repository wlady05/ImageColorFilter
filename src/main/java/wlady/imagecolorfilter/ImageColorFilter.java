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

package wlady.imagecolorfilter;

import java.util.Arrays;

import java.util.stream.IntStream;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImageColorFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ImageColorFilter.class);

    /**
     * Filters (each element is between 0.0 and 1.0).
     */
    protected double[] filter;

    /**
     * The filtered image;
     */
    protected WritableImage filteredImage;

    /**
     * The pixel reader, that is used to read the original image.
     */
    protected PixelReader pixelReader;

    /**
     * The pixel writer, that is used to write into the filtered image.
     */
    protected PixelWriter pixelWriter;

    /**
     * The pixel filter function.
     *
     * @param x
     * @param y
     */
    protected abstract void filterPixel(int x, int y);

    /**
     * Returns the filtered image.
     *
     * @return
     * the filtered image
     */
    public Image getImage() {
        return filteredImage;
    }

    /**
     * Filter colors of the original image.
     *
     * @param image
     * is the image, that is to be filtered
     *
     * @param filter
     * are filter's parameters
     */
    public void filterImage(Image image, double[] filter) {
        if (image == null) {
            filteredImage = null;

            LOG.info("filterImage(): called without image");
            return ;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        this.filter = Arrays.copyOf(filter, filter.length);

        filteredImage = new WritableImage(width, height);

        pixelReader = image.getPixelReader();
        pixelWriter = filteredImage.getPixelWriter();

        StopWatch stopWatch;

        stopWatch = new StopWatch();
        stopWatch.start();

        IntStream
            .range(0, width * height)
            .parallel()
            .forEach(xy -> filterPixel(xy % width, xy / width));

        stopWatch.stop();

        // There is no need to keep these
        pixelReader = null;
        pixelWriter = null;

        LOG.info("filterImage(): called for {}x{}px image, time to complete {}", width, height, stopWatch);
    }
}
