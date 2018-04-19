package org.dulab.site.controllers;

import org.dulab.models.Peak;

import java.util.List;

public class ControllerUtils {

    public static String peaksToJson(List<Peak> peaks) {

        if (peaks == null) return "";

        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(0.0);

        if (maxIntensity <= 0.0) return "";

        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < peaks.size(); ++i) {

            if (i != 0)
                stringBuilder.append(',');

            stringBuilder.append('[')
                    .append(peaks.get(i).getMz())
                    .append(',')
                    .append(100 * peaks.get(i).getIntensity() / maxIntensity)
                    .append(']');
        }
        stringBuilder.append(']');

        return stringBuilder.toString();
    }
}
