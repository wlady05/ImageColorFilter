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

import com.sun.javafx.Utils;

public class ImageColorHistogramHsb extends ImageColorHistogram {
    private static final double SATURATION = 255.0;
    private static final double BRIGHTNESS = 255.0;

    public static final Description[] HISTOGRAM_DESCRIPTION =
    {
        new Description("Hue", 361),
        new Description("Saturation", (int) SATURATION + 1),
        new Description("Brightness", (int) BRIGHTNESS + 1)
    } ;

    public ImageColorHistogramHsb() {
        super(HISTOGRAM_DESCRIPTION);
    }

    @Override
    protected void calculateHistograms(int x, int y) {
        //
        // This implementation is somehow faster than using pixelReader.getColor() and javafx.scene.paint.Color and it's
        // getHue(), getSaturation() and getBrightness() methods. This is so because of Color's implementation of these
        // methods, that in 8u40 version just call Utils.RGBtoHSB() - it turns out, that to get these 3 components,
        // there are 3 calls to RGBtoHSB(), which seems to be pretty expensive.

        int argb = pixelReader.getArgb(x, y);

        int cr = (argb >> 16) & 0x00ff;
        int cg = (argb >>  8) & 0x00ff;
        int cb = (argb      ) & 0x00ff;

        double[] hsb = Utils.RGBtoHSB(cr / 255.0, cg / 255.0, cb / 255.0);

        int h = (int) Math.round(hsb[0]);
        int s = (int) Math.round(hsb[1] * SATURATION);
        int b = (int) Math.round(hsb[2] * BRIGHTNESS);

        histograms[0][h]++;
        histograms[1][s]++;
        histograms[2][b]++;
    }

    @Override
    protected void processHistograms() {
        histograms[1][0] = 0;
        histograms[2][0] = 0;
    }
}
