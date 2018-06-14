package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SampleSourceType;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.SubmissionCategory;

import javax.json.*;
import javax.validation.constraints.NotBlank;
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

    public static JsonObject spectrumToJson(Spectrum spectrum) {

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("name", spectrum.getName());

        double maxIntensity = spectrum.getPeaks().stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(0.0);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        if (maxIntensity > 0.0)
            spectrum.getPeaks()
                    .forEach(p -> jsonArrayBuilder.add(
                            Json.createObjectBuilder()
                                    .add("mz", p.getMz())
                                    .add("intensity", 100 * p.getIntensity() / maxIntensity)
                                    .build()
                    ));

        jsonObjectBuilder.add("peaks", jsonArrayBuilder.build());

        return jsonObjectBuilder.build();
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
//                                        .filter(s -> s.getSource() == type)
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


    public static class CategoryForm {

        @NotBlank(message = "The field Name is required")
        private String name;

        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }


    public static class CategoryWithSubmissionCount {

        private final SubmissionCategory category;
        private final long count;

        CategoryWithSubmissionCount(SubmissionCategory category, long count) {
            this.category = category;
            this.count = count;
        }

        public SubmissionCategory getCategory() {
            return category;
        }

        public long getCount() {
            return count;
        }
    }
}
