package top.orderly.noticeexpress.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for formatting timestamps.
 */
public class TimeFormatter {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    /**
     * Formats a timestamp to "yyyy/MM/dd HH:mm" format.
     */
    public static String formatDateTime(long timestamp) {
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    /**
     * Formats a timestamp to "yyyy/MM/dd" format.
     */
    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    /**
     * Formats a timestamp to "HH:mm" format.
     */
    public static String formatTime(long timestamp) {
        return TIME_FORMAT.format(new Date(timestamp));
    }

    /**
     * Gets the current timestamp in milliseconds.
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}