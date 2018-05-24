package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SampleSourceType;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;

import javax.json.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    public static JsonArray getPieChartData(SpectrumCluster cluster) {

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        Arrays.stream(SampleSourceType.values())
                .forEachOrdered(type -> jsonArrayBuilder.add(
                        Json.createObjectBuilder()
                                .add("label", type.getLabel())
                                .add("count", cluster.getSpectra()
                                        .stream()
                                        .map(Spectrum::getSubmission)
                                        .filter(Objects::nonNull)
                                        .filter(s -> s.getSampleSourceType() == type)
                                        .count())
                                .build()));

        return jsonArrayBuilder.build();
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
