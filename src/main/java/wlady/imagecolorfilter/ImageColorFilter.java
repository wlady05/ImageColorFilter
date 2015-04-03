/*
 * (c) 2015 wlady
 */

package wlady.imagecolorfilter;

import java.util.stream.IntStream;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageColorFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ImageColorFilter.class);

    /**
     * The original image.
     */
    private final Image originalImage;

    /**
     * Red component filter (between 0.0 and 1.0).
     */
    private final double rFilter;

    /**
     * Green component filter (between 0.0 and 1.0).
     */
    private final double gFilter;

    /**
     * Blue component filter (between 0.0 and 1.0).
     */
    private final double bFilter;

    /**
     * Image width.
     */
    private final int imageWidth;

    /**
     * Image height.
     */
    private final int imageHeight;

    /**
     * The filtered image;
     */
    private WritableImage filteredImage;

    /**
     * The pixel reader, that is used to read the original image.
     */
    private PixelReader pixelReader;

    /**
     * The pixel writer, that is used to write into the filtered image.
     */
    private PixelWriter pixelWriter;

    public ImageColorFilter(Image originalImage, double rFilter, double gFilter, double bFilter) {
        this.originalImage = originalImage;

        this.rFilter = rFilter;
        this.gFilter = gFilter;
        this.bFilter = bFilter;

        if (originalImage == null) {
            imageWidth = 0;
            imageHeight = 0;
        } else {
            imageWidth = (int) originalImage.getWidth();
            imageHeight = (int) originalImage.getHeight();
        }
    }

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
     */
    public void filterImage() {
        if (originalImage == null) {
            filteredImage = null;

            LOG.info("filterImage(): called without image");
            return ;
        }

        filteredImage = new WritableImage(imageWidth, imageHeight);

        pixelReader = originalImage.getPixelReader();
        pixelWriter = filteredImage.getPixelWriter();

        StopWatch stopWatch;

        stopWatch = new StopWatch();
        stopWatch.start();

        IntStream.range(0, imageWidth * imageHeight).parallel().forEach(xy -> {
            int x = xy % imageWidth;
            int y = xy / imageWidth;

            int argb = pixelReader.getArgb(x, y);

            int r = (argb >> 16) & 0x00ff;
            int g = (argb >>  8) & 0x00ff;
            int b = (argb      ) & 0x00ff;

            r = Math.min((int) Math.round(r * rFilter), 255);
            g = Math.min((int) Math.round(g * gFilter), 255);
            b = Math.min((int) Math.round(b * bFilter), 255);

            argb = (argb & 0xff000000) | (r << 16) | (g << 8) | b;

            pixelWriter.setArgb(x, y, argb);
        });

        stopWatch.stop();

        pixelReader = null;
        pixelWriter = null;

        LOG.info("filterImage(): called for {}x{}px image, time to complete {}", imageWidth, imageHeight, stopWatch);
    }
}
