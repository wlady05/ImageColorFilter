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

public class ImageColorHistogramRgb extends ImageColorHistogram {
    public static final Description[] HISTOGRAM_DESCRIPTION =
    {
        new Description("Red", 256),
        new Description("Green", 256),
        new Description("Blue", 256)
    } ;

    public ImageColorHistogramRgb() {
        super(HISTOGRAM_DESCRIPTION);
    }

    @Override
    protected void calculateHistograms(int x, int y) {
        int argb = pixelReader.getArgb(x, y);

        int r = (argb >> 16) & 0x00ff;
        int g = (argb >>  8) & 0x00ff;
        int b = (argb      ) & 0x00ff;

        histograms[0][r]++;
        histograms[1][g]++;
        histograms[2][b]++;
    }

    @Override
    protected void processHistograms() {
        histograms[0][0] = 0;
        histograms[1][0] = 0;
        histograms[2][0] = 0;
    }
}
