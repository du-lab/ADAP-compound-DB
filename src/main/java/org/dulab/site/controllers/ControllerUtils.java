package org.dulab.site.controllers;

import org.dulab.models.entities.Peak;

import javax.json.*;
import java.util.Comparator;
import java.util.List;

public class ControllerUtils {

    public static JsonArray peaksToJson(List<Peak> peaks) {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        if (peaks == null) return builder.build();

        peaks.sort(Comparator.comparingDouble(p -> -p.getIntensity()));

        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(0.0);

        if (maxIntensity <= 0.0) return builder.build();

        for (Peak peak : peaks)
            builder.add(
                    Json.createArrayBuilder()
                            .add(peak.getMz())
                            .add(100 * peak.getIntensity() / maxIntensity));

        return builder.build();
    }

    public static int getEntryIndex(List list, Object entry) {

        if (list == null) return -1;

        for (int i = 0; i < list.size(); ++i)
            if (entry.equals(list.get(i)))
                return i;

        return -1;
    }

    public static int toIntegerScore(float score) {
        return Math.round(1000 * score);
    }
}
