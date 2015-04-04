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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImageColorHistogram {
    private static final Logger LOG = LoggerFactory.getLogger(ImageColorHistogram.class);

    /**
     * Describes one histogram - it's name and bins count.
     */
    public static class Description {
        private final String name;

        private final int binCount;

        public Description(String name, int binCount) {
            this.name = name;
            this.binCount = binCount;
        }

        public String getName() {
            return name;
        }

        public int getBinCount() {
            return binCount;
        }
    }

    /**
     * Histograms.
     */
    protected final int[][] histograms;

    /**
     * The pixel reader, that is used to read the image.
     */
    protected PixelReader pixelReader;

    /**
     * The function, that calculates histograms.
     *
     * @param x
     * @param y
     */
    protected abstract void calculateHistograms(int x, int y);

    /**
     * Called after the histograms are calculated.
     *
     * <p>
     * Could be used to make changes to or some additional processing of calculated histograms.
     */
    protected abstract void processHistograms();

    /**
     * The constructor.
     *
     * @param histogramDescriptions
     * the description of histograms - number and bin counts
     */
    public ImageColorHistogram(Description[] histogramDescriptions) {
        histograms = new int[histogramDescriptions.length][];

        for (int i = 0; i < histogramDescriptions.length; i++) {
            histograms[i] = new int[histogramDescriptions[i].getBinCount()];
        }
    }

    /**
     * Calculates the color histograms for the image.
     *
     * <p>
     * If the {@code image} is null, the histograms will just be cleared.
     *
     * @param image
     * is the image
     */
    public void calculateHistograms(Image image) {
        for (int[] histogram : histograms) {
            Arrays.fill(histogram, 0);
        }

        if (image == null) {
            LOG.info("calculateHistograms(): called without image");
            return ;
        }

        pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        StopWatch stopWatch;

        stopWatch = new StopWatch();
        stopWatch.start();

        IntStream
            .range(0, width * height)
            .forEach(xy -> calculateHistograms(xy % width, xy / width));

        processHistograms();

        stopWatch.stop();

        // There is no need to keep reference to this
        pixelReader = null;

        LOG.info("calculateHistograms(): called for {}x{}px image, time to complete {}", width, height, stopWatch);
    }

    public int[] getHistogram(int index) {
        return histograms[index];
    }
}
