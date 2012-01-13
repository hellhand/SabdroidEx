package com.utils;

import java.text.DecimalFormat;

public class Formatter {

    private static final ThreadLocal<DecimalFormat> fullFormat = new ThreadLocal<DecimalFormat>() {

        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("###,###,###,###.##");
        }
    };

    private static final ThreadLocal<DecimalFormat> shortFormat = new ThreadLocal<DecimalFormat>() {

        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("###,###,###,###");
        }
    };

    public static String formatShort(double value) {
        return shortFormat.get().format(value);
    }

    public static String formatShort(String value) {
        return formatShort(Double.parseDouble(value));
    }

    public static String formatFull(double value) {
        return fullFormat.get().format(value);
    }

    public static String formatFull(String value) {
        return formatFull(Double.parseDouble(value));
    }
}
