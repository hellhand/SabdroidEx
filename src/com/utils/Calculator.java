package com.utils;

public class Calculator {

    private static final long MILLISECONDS = 1000;
    private static final long SECONDS = 60;
    private static final long MINUTES = 60;

    public static String calculateETA(double size, double speed) {
        if (speed <= 0) {
            return "---";
        }

        size *= 1024;
        double eta = (size / speed) * MILLISECONDS;

        return formatMillisIntoHumanReadable(Math.round(eta));
    }

    private static String formatMillisIntoHumanReadable(long time) {
        time /= MILLISECONDS;
        int seconds = (int) (time % SECONDS);
        time /= SECONDS;
        int minutes = (int) (time % MINUTES);
        time /= MINUTES;
        int hours = (int) (time % 24);
        int days = (int) (time / 24);
        if (days == 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        else {
            return String.format("%dd%d:%02d:%02d", days, hours, minutes, seconds);
        }
    }
}
