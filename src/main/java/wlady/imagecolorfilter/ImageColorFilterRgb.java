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

public class ImageColorFilterRgb extends ImageColorFilter {
    public ImageColorFilterRgb() {
        // Empty
    }

    @Override
    protected void filterPixel(int x, int y) {
        int argb = pixelReader.getArgb(x, y);

        int r = (argb >> 16) & 0x00ff;
        int g = (argb >>  8) & 0x00ff;
        int b = (argb      ) & 0x00ff;

        r = (int) Math.round(r * filter[0]);
        g = (int) Math.round(g * filter[1]);
        b = (int) Math.round(b * filter[2]);

        argb = (argb & 0xff000000) | (r << 16) | (g << 8) | b;

        pixelWriter.setArgb(x, y, argb);
    }
}
