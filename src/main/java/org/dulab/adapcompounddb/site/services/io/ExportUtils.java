package org.dulab.adapcompounddb.site.services.io;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExportUtils {

    private final static String CHECK_CHARACTER = String.valueOf('\u2713');

    public static String formatBoolean(boolean x) {
        return x ? CHECK_CHARACTER : null;
    }

    public static String formatDouble(Double x, int digits) {
        if (x == null)
            return null;
        String format = String.format("%%.%df", digits);
        return String.format(format, x);
    }

    public static String formatDoubleArray(double[] xs, int digits) {
        if (xs == null)
            return null;
        return Arrays.stream(xs).mapToObj(x -> formatDouble(x, digits))
                .collect(Collectors.joining(", "));
    }

    public static String formatStringArray(String[] strings) {
        if (strings == null)
            return null;
        return String.join(", ", strings);
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
