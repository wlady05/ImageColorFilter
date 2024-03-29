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

import java.util.Locale;
import java.util.Calendar;

import java.text.SimpleDateFormat;

public class StopWatch {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("00:mm:ss.SSS", Locale.ENGLISH);

    private long startTime = 0;
    private long stopTime = 0;

    public StopWatch() {
        // Empty
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        stopTime = System.nanoTime();
    }

    /**
     * Get elapsed time in milliseconds.
     *
     * @return
     * elapsed time in milliseconds
     */
    public long getElapsedTime() {
        return (stopTime - startTime) / 1_000_000L;
    }

    public String format(long time) {
        Calendar cal;

        cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        return FORMAT.format(cal.getTime());
    }

    @Override
    public String toString() {
        return format(getElapsedTime());
    }
}
