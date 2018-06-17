package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.SubmissionCategory;

import javax.json.*;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ControllerUtils {

    private static String getColor(int n) {
        final int colorStringLength = 6;
        final String colors = "1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf";

        int index = n * colorStringLength % colors.length();
        String color = colors.substring(index, index + colorStringLength);

        return String.format("#%s;", color);
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

    public static JsonArray clusterSourceToJson(SpectrumCluster cluster, List<SubmissionSource> sources) {

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        // Count Spectra with the no source
        jsonArrayBuilder.add(
                Json.createObjectBuilder()
                        .add("label", "Undefined")
                        .add("count", cluster.getSpectra()
                                .stream()
                                .map(Spectrum::getSubmission)
                                .filter(Objects::nonNull)
                                .filter(s -> s.getSource() == null)
                                .count()));

        // Count Spectra for each source
        for (SubmissionSource source : sources)
            jsonArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("label", source.getName())
                            .add("count", cluster.getSpectra()
                                    .stream()
                                    .map(Spectrum::getSubmission)
                                    .filter(Objects::nonNull)
                                    .map(Submission::getSource)
                                    .filter(Objects::nonNull)
                                    .filter(s -> s.equals(source))
                                    .count()));

        return jsonArrayBuilder.build();
    }

    public static JsonArray clusterSpecimenToJson(SpectrumCluster cluster, List<SubmissionSpecimen> species) {

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        // Count Spectra with the no source
        jsonArrayBuilder.add(
                Json.createObjectBuilder()
                        .add("label", "Undefined")
                        .add("count", cluster.getSpectra()
                                .stream()
                                .map(Spectrum::getSubmission)
                                .filter(Objects::nonNull)
                                .filter(s -> s.getSpecimen() == null)
                                .count()));

        // Count Spectra for each source
        for (SubmissionSpecimen specimen : species)
            jsonArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("label", specimen.getName())
                            .add("count", cluster.getSpectra()
                                    .stream()
                                    .map(Spectrum::getSubmission)
                                    .filter(Objects::nonNull)
                                    .map(Submission::getSpecimen)
                                    .filter(Objects::nonNull)
                                    .filter(s -> s.equals(specimen))
                                    .count()));

        return jsonArrayBuilder.build();
    }

    public static JsonArray clusterDiseaseToJson(SpectrumCluster cluster, List<SubmissionDisease> diseases) {

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        // Count Spectra with the no source
        jsonArrayBuilder.add(
                Json.createObjectBuilder()
                        .add("label", "Undefined")
                        .add("count", cluster.getSpectra()
                                .stream()
                                .map(Spectrum::getSubmission)
                                .filter(Objects::nonNull)
                                .filter(s -> s.getDisease() == null)
                                .count()));

        // Count Spectra for each source
        for (SubmissionDisease disease : diseases)
            jsonArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("label", disease.getName())
                            .add("count", cluster.getSpectra()
                                    .stream()
                                    .map(Spectrum::getSubmission)
                                    .filter(Objects::nonNull)
                                    .map(Submission::getDisease)
                                    .filter(Objects::nonNull)
                                    .filter(s -> s.equals(disease))
                                    .count()));

        return jsonArrayBuilder.build();
    }

    public static String jsonToHtml(JsonArray jsonArray) {

        int totalCount = 0;
        for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class))
            totalCount += jsonObject.getInt("count");

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
            if (jsonObject.getInt("count") > 0) {
                int percent = 100 * jsonObject.getInt("count") / totalCount;
                builder.append(
                        String.format("%s: %d&percnt;<br/><div style=\"width: %d&percnt;; height: 2px; background-color: %s;\"></div>\n",
                                jsonObject.getString("label"),
                                percent,
                                percent,
                                getColor(count)));
            }
            ++count;
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
