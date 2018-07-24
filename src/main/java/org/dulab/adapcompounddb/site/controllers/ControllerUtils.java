package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;

import javax.json.*;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ControllerUtils {

    private static String getColor(int n) {
        final int colorStringLength = 6;
        final String colors = "1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf";

        int index = n * colorStringLength % colors.length();
        String color = colors.substring(index, index + colorStringLength);

        return String.format("#%s;", color);
    }

    public static JsonArray stringsToJson(List<String> strings) {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        if (strings == null) return builder.build();

        for (String s : strings)
            builder.add(s);

        return builder.build();
    }

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

    public static JsonArray clusterDistributionToJson(List<Spectrum> spectra,
                                                      List<SubmissionCategory> categories) {

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        if (categories == null)
            return jsonArrayBuilder.build();

        for (SubmissionCategory category : categories) {

            long numSpectraInCluster = spectra.stream()
                    .map(Spectrum::getFile)
                    .filter(Objects::nonNull)
                    .map(File::getSubmission)
                    .filter(Objects::nonNull)
                    .map(s -> s.getCategory(category.getCategoryType()))
                    .filter(Objects::nonNull)
                    .filter(category::equals)
                    .count();

            if (numSpectraInCluster == 0) continue;

            long numSpectraInTotal = category.getSubmissions()
                    .stream()
                    .mapToLong(s -> s.getFiles().size())
                    .sum();

            jsonArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("label", category.getName())
                            .add("count", (double) numSpectraInCluster / numSpectraInTotal)
                            .build());
        }

        return jsonArrayBuilder.build();
    }

    public static String jsonToHtml(JsonArray jsonArray) {

        double totalCount = 0;
        for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class))
            totalCount += jsonObject.getJsonNumber("count").doubleValue();

        StringBuilder builder = new StringBuilder();
        int color = 0;
        for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {

            double count = jsonObject.getJsonNumber("count").doubleValue();

            if (count > 0.0) {
                long percent = Math.round(100 * count / totalCount);
                builder.append(
                        String.format("%s: %d&percnt;<br/><div style=\"width: %d&percnt;; height: 2px; background-color: %s;\"></div>\n",
                                jsonObject.getString("label"),
                                percent,
                                percent,
                                getColor(color)));
            }

            ++color;
        }
        return builder.toString();
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
