/*
 * (c) 2015 wlady
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
