package org.dulab.adapcompounddb.site.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.constraints.NotBlank;

import org.apache.commons.collections.CollectionUtils;
import org.dulab.adapcompounddb.models.UserRole;
import org.dulab.adapcompounddb.models.dto.TagInfo;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.site.services.SpectrumClusterer;
import org.dulab.adapcompounddb.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

public class ControllerUtils {

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

        final double maxIntensity = spectrum.getPeaks().stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(0.0);

        final JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        if (maxIntensity > 0.0) {
            spectrum.getPeaks()
            .forEach(p -> jsonArrayBuilder.add(
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

        final List<TagInfo> tagInfoList = getDiversityIndices(spectra);
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

    public static List<TagInfo> getDiversityIndices(final List<Spectrum> spectra) {

        final List<String> tagList = new ArrayList<>();

        for(final Spectrum s: spectra) {
            for(final SubmissionTag tag: s.getFile().getSubmission().getTags()) {
                tagList.add(tag.getId().getName());
            }
        }

        final Map<String, List<String>> tagMap = new HashMap<>(); // source:<src1, src2, src1, src2>

        tagList.forEach(tag -> {
            final String[] arr = tag.split(":", 2);
            if(arr.length == 2) {
                final String key = arr[0].trim();
                final String value = arr[1].trim();

                List<String> valueList = tagMap.get(key);
                if(CollectionUtils.isEmpty(valueList)) {
                    valueList = new ArrayList<>();
                    tagMap.put(key, valueList);
                }
                valueList.add(value);
            }
        });
        final List<TagInfo> tagInfoList = new ArrayList<>();

        for(final Entry<String, List<String>> entry : tagMap.entrySet()) {
            final TagInfo tagInfo = new TagInfo();
            final double diversity = MathUtils.diversityIndex(entry.getValue());
            //            diversityMap.put(entry.getKey(), diversity);

            tagInfo.setName(entry.getKey());
            tagInfo.setDiversity(diversity);

            final Map<String, Integer> countMap = new HashMap<>();
            entry.getValue().forEach(tag -> {
                Integer count = countMap.get(tag);
                if(count == null) {
                    count = 0;
                    countMap.put(tag, count);
                }
                count++;
            });
            tagInfo.setCountMap(countMap);
            tagInfoList.add(tagInfo);
        };

        return tagInfoList;
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
