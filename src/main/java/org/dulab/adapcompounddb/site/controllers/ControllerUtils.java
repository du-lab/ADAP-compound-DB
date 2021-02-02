package org.dulab.adapcompounddb.site.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchForm;
import org.dulab.adapcompounddb.models.enums.UserRole;
import org.dulab.adapcompounddb.models.dto.TagInfo;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.SpectrumClusterer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.json.*;
import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerUtils {

    public static final String GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME = "group_search_results";
    public static final String INDIVIDUAL_SEARCH_RESULTS_ATTRIBUTE_NAME = "individual_search_results";


    @Autowired
    SpectrumClusterer spectrumClusterer;

    private static final String ROLE_ADMIN = "ROLE_" + UserRole.ADMIN.name();

    private static String getColor(final int n) {
        final int colorStringLength = 6;
        final String colors = "1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf";

        final int index = n * colorStringLength % colors.length();
        final String color = colors.substring(index, index + colorStringLength);

        return String.format("#%s;", color);
    }

    public static QueryParameters getParameters(final SearchForm form) {
        final QueryParameters parameters = new QueryParameters();
        final String tags = form.getTags();
        parameters.setScoreThreshold(form.isScoreThresholdCheck() ? form.getFloatScoreThreshold() : null);
        parameters.setMzTolerance(form.isScoreThresholdCheck() ? form.getMzTolerance() : null);
        parameters.setPrecursorTolerance(form.isMassToleranceCheck() ? form.getMassTolerance() : null);
        parameters.setRetTimeTolerance(form.isRetTimeToleranceCheck() ? form.getRetTimeTolerance() : null);
        parameters.setTags(tags != null && tags.length() > 0 ? new HashSet<>(Arrays.asList(tags.split(","))) : null);
        return parameters;
    }

    public static JsonArray stringsToJson(final List<String> strings) {

        final JsonArrayBuilder builder = Json.createArrayBuilder();

        if (strings == null) {
            return builder.build();
        }

        for (final String s : strings) {
            builder.add(s);
        }

        return builder.build();
    }

    public static JsonArray peaksToJson(final List<Peak> peaks) {

        final JsonArrayBuilder builder = Json.createArrayBuilder();

        if (peaks == null) {
            return builder.build();
        }

        peaks.sort(Comparator.comparingDouble(p -> -p.getIntensity()));

        final double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(0.0);

        if (maxIntensity <= 0.0) {
            return builder.build();
        }

        for (final Peak peak : peaks) {
            builder.add(
                    Json.createArrayBuilder()
                            .add(peak.getMz())
                            .add(100 * peak.getIntensity() / maxIntensity));
        }

        return builder.build();
    }

    public static JsonObject spectrumToJson(final Spectrum spectrum) {

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("name", spectrum.getName().replace("'", ""));

        final JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        List<Peak> peaks = spectrum.getPeaks();
        if (peaks != null) {

            double maxIntensity = peaks.stream()
                    .mapToDouble(Peak::getIntensity)
                    .max()
                    .orElse(0.0);

            peaks.forEach(p -> jsonArrayBuilder.add(
                            Json.createObjectBuilder()
                                    .add("mz", p.getMz())
                                    .add("intensity", 100 * p.getIntensity() / maxIntensity)
                                    .build()
                    ));
        }

        jsonObjectBuilder.add("peaks", jsonArrayBuilder.build());

        return jsonObjectBuilder.build();
    }

    public static JsonArray clusterDistributionToJson(final List<Spectrum> spectra,
                                                      final List<SubmissionCategory> categories) {

        final JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        if (categories == null) {
            return jsonArrayBuilder.build();
        }

        for (final SubmissionCategory category : categories) {

            final long numSpectraInCluster = spectra.stream()
                    .map(Spectrum::getFile)
                    .filter(Objects::nonNull)
                    .map(File::getSubmission)
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(s -> s.getCategory(category.getCategoryType()))
                    .filter(Objects::nonNull)
                    .filter(category::equals)
                    .count();

            if (numSpectraInCluster == 0) {
                continue;
            }

            final long numSpectraInTotal = category.getSubmissions()
                    .stream()
                    .mapToLong(s -> s.getFiles().size())
                    .sum();

            jsonArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("label", category.getName())
                            .add("count", (double) numSpectraInCluster / numSpectraInTotal)
                            .build());

            jsonArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("label", category.getName() + numSpectraInCluster)
                            .add("count", (double) numSpectraInCluster / numSpectraInTotal)
                            .build());
        }

        return jsonArrayBuilder.build();
    }

    public static String clusterTagsToJson(final List<Spectrum> spectra) {

        final List<TagInfo> tagInfoList = getDiversityIndicesDeprecated(spectra);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final Gson gson = new Gson();

        final List<Map<String, Object>> pieChart = new ArrayList<>(); // source: {src1: 2, src2: 2}

        tagInfoList.forEach(tagInfo -> {
            final Map<String, Object> tag = new HashMap<>();
            tag.put("name", tagInfo.getName());
            tag.put("diversity", tagInfo.getDiversity());

            final List<Map<String, String>> elementList = new ArrayList<>();
            tagInfo.getCountMap().forEach((l, c) -> {
                final Map<String, String> element = new HashMap<>();
                final Integer count = c.intValue();
                element.put("label", l);
                element.put("count", count.toString());
                elementList.add(element);
            });
            tag.put("values", elementList);
            pieChart.add(tag);
        });

        return gson.toJson(pieChart, List.class);
    }

    @Deprecated
    public static List<TagInfo> getDiversityIndicesDeprecated(List<Spectrum> spectra) {
        List<Submission> submissions = spectra.stream()
                .map(Spectrum::getFile).filter(Objects::nonNull)
                .map(File::getSubmission).filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return getDiversityIndices(submissions);
    }

    public static List<TagInfo> getDiversityIndices(List<Submission> submissions) {

        // Find unique keys among all tags of all spectra
//        final List<String> keys = spectra.stream()
//                .map(Spectrum::getFile).filter(Objects::nonNull)
//                .map(File::getSubmission).filter(Objects::nonNull)
//                .distinct()
        List<String> keys = submissions.stream()
                .flatMap(s -> s.getTags().stream())
                .map(SubmissionTag::getTagKey)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

//        Set<Submission> submissions = spectra.stream()
//                .map(Spectrum::getFile).filter(Objects::nonNull)
//                .map(File::getSubmission).filter(Objects::nonNull)
//                .collect(Collectors.toSet());

        // For each key, find its values and their count
        List<TagInfo> tagInfoList = new ArrayList<>(keys.size());
        for (String key : keys) {

            Map<String, Integer> countMap = new HashMap<>();
            for (Submission submission : submissions) {

                List<String> tagValues = submission.getTags().stream()
                        .filter(t -> t.getTagKey().equalsIgnoreCase(key))
                        .map(SubmissionTag::getTagValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (tagValues.isEmpty())
                    countMap.compute("undefined", (k, v) -> (v == null) ? 1 : v + 1);
                else {
                    for (String value : tagValues)
                        countMap.compute(value, (k, v) -> (v == null) ? 1 : v + 1);
                }

            }

            // Save values and their counts to TagInfo
            TagInfo tagInfo = new TagInfo();
            tagInfo.setName(key);
            tagInfo.setCountMap(countMap);
            tagInfo.setDiversity(getDiversity(countMap));

            tagInfoList.add(tagInfo);
        }

        return tagInfoList;
    }


    private static List<String> getTags(Spectrum spectrum) {

        if (spectrum.getFile() != null && spectrum.getFile().getSubmission() != null) {

            List<SubmissionTag> tags = spectrum.getFile().getSubmission().getTags();
            return tags.stream()
                    .map(SubmissionTag::toString)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>(0);
    }

    private static double getDiversity(Map<String, Integer> countMap) {

        Collection<Integer> counts = countMap.values();

        int total = counts.stream()
                .mapToInt(Integer::intValue)
                .sum();

        double proportionSum = counts.stream()
                .mapToDouble(Integer::doubleValue)
                .map(c -> (c / total) * (c / total))
                .sum();

        return 1 - proportionSum;
    }

    private static TagInfo stringToTagInfoWithName(String tag) {

        if (tag == null) return null;

        String[] values = tag.split(":");
        if (values.length < 2)
            return null;

        TagInfo tagInfo = new TagInfo();
        tagInfo.setName(values[0].trim());

        return tagInfo;
    }

    public static String jsonToHtml(final JsonArray jsonArray) {

        double totalCount = 0;
        for (final JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {
            totalCount += jsonObject.getJsonNumber("count").doubleValue();
        }

        final StringBuilder builder = new StringBuilder();
        int color = 0;
        for (final JsonObject jsonObject : jsonArray.getValuesAs(JsonObject.class)) {

            final double count = jsonObject.getJsonNumber("count").doubleValue();

            if (count > 0.0) {
                final long percent = Math.round(100 * count / totalCount);
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

    public static String significanceBar(final double average, final double min, final double max) {
        final double start = -3;
        final double end = 3;

        final long avePercent = Math.round(100 * (average - start) / (end - start));
        final long minPercent = Math.round(100 * (min - start) / (end - start));
        final long maxPercent = Math.round(100 * (max - start) / (end - start));

        String html = "<div>";

        // Grey area
        html += String.format(
                "<div style=\"display: inline-block; width: %d&percnt;; height: 2px; background-color: #f2f2f2;\"></div>",
                minPercent);

        // Blue area
        html += String.format(
                "<div style=\"display: inline-block; width: %d&percnt;; height: 2px; background-color: blue;\"></div>",
                avePercent - minPercent);

        // Red area
        html += String.format(
                "<div style=\"display: inline-block; width: %d&percnt;; height: 2px; background-color: red;\"></div>",
                maxPercent - avePercent);

        // Grey area
        html += String.format(
                "<div style=\"display: inline-block; width: %d&percnt;; height: 2px; background-color: #f2f2f2;\"></div>",
                100 - maxPercent);

        html += "</div>";

        return html;
    }

    public static int getEntryIndex(final List<?> list, final Object entry) {

        if (list == null) {
            return -1;
        }

        for (int i = 0; i < list.size(); ++i) {
            if (entry.equals(list.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static int toIntegerScore(final float score) {
        return Math.round(1000 * score);
    }


    public static boolean isAdmin(final User user) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase(ROLE_ADMIN));
    }


    public static class CategoryForm {

        @NotBlank(message = "The field Name is required")
        private String name;

        private String description;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }
    }


    public static class CategoryWithSubmissionCount {

        private final SubmissionCategory category;
        private final long count;

        CategoryWithSubmissionCount(final SubmissionCategory category, final long count) {
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
