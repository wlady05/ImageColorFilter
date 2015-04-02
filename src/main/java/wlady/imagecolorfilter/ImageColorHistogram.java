/*
 * (c) 2015 wlady
 */

package wlady.imagecolorfilter;

import java.util.stream.IntStream;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class ImageColorHistogram {
    public static final int BUCKET_COUNT = 255;

    /**
     * Histogram of red color.
     */
    int[] rHistogram;

    /**
     * Histogram of green color.
     */
    int[] gHistogram;

    /**
     * Histogram of blue color.
     */
    int[] bHistogram;

    public ImageColorHistogram() {
        rHistogram = new int[BUCKET_COUNT];
        gHistogram = new int[BUCKET_COUNT];
        bHistogram = new int[BUCKET_COUNT];
    }

    /**
     * Calculates the color histograms for the image.
     *
     * @param image
     * is the image, that could be {@code null}
     */
    public void calculateColorHistograms(Image image) {
        for (int i = 0; i < BUCKET_COUNT; i++) {
            rHistogram[i] = 0;
            gHistogram[i] = 0;
            bHistogram[i] = 0;
        }

        if (image == null) {
            return ;
        }

        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        IntStream.range(0, width * height).forEach(xy -> {
            int x = xy % width;
            int y = xy / width;

            int argb = pixelReader.getArgb(x, y);

            int r = (argb >> 16) & 0x00ff;
            int g = (argb >>  8) & 0x00ff;
            int b = (argb      ) & 0x00ff;

            if (r > 0) {
                rHistogram[r - 1]++;
            }
            if (g > 0) {
                gHistogram[g - 1]++;
            }
            if (b > 0) {
                bHistogram[b - 1]++;
            }
        });
    }

    public int[] getRed() {
        return rHistogram;
    }

    public int[] getGreen() {
        return gHistogram;
    }

    public int[] getBlue() {
        return bHistogram;
    }
}
