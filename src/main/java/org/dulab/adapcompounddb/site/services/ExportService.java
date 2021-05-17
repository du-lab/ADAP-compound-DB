package org.dulab.adapcompounddb.site.services;

import com.opencsv.CSVWriter;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportService {

    private final SpectrumService spectrumService;

    @Autowired
    public ExportService(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    public void exportToCSV(OutputStream outputStream, List<SearchResultDTO> searchResults) {
        exportAllToCSV(outputStream, selectTopResults(searchResults));
    }

    public void exportAllToCSV(OutputStream outputStream, List<SearchResultDTO> searchResults) {

        long[] matchIds = searchResults.stream()
                .mapToLong(SearchResultDTO::getSpectrumId)
                .toArray();

        // Retrieve spectrum properties
        SortedSet<String> propertyNames = new TreeSet<>();
        Map<Long, List<SpectrumProperty>> spectrumIdToPropertiesMap = new HashMap<>();
        if (matchIds != null && matchIds.length > 0) {
            List<SpectrumProperty> properties = spectrumService.findSpectrumPropertiesBySpectrumId(matchIds);
            for (SpectrumProperty property : properties) {
                propertyNames.add(property.getName());
                spectrumIdToPropertiesMap
                        .computeIfAbsent(property.getSpectrum().getId(), k -> new ArrayList<>())
                        .add(property);
            }
        }

        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream));
        csvWriter.writeNext(createHeader(propertyNames));
        for (SearchResultDTO searchResult : searchResults) {
            csvWriter.writeNext(createRow(propertyNames, spectrumIdToPropertiesMap, searchResult));
        }
    }

    private List<SearchResultDTO> selectTopResults(List<SearchResultDTO> searchResults) {

        long[] queryIds = searchResults.stream()
                .mapToLong(ExportService::getQueryId)
                .distinct()
                .sorted()
                .toArray();

        List<SearchResultDTO> topResults = new ArrayList<>();
        for (long queryId : queryIds) {

            List<SearchResultDTO> selectedSearchResults = searchResults.stream()
                    .filter(r -> queryId == getQueryId(r))
                    .collect(Collectors.toList());

            if (selectedSearchResults.isEmpty()) continue;

            selectedSearchResults.sort(Comparator
                    .comparing(SearchResultDTO::getOntologyPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(SearchResultDTO::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(SearchResultDTO::getMassErrorPPM, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(SearchResultDTO::getMassError, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(SearchResultDTO::getRetTimeError, Comparator.nullsLast(Comparator.naturalOrder())));

            topResults.add(selectedSearchResults.get(0));
        }

//        IntStream.range(0, topResults.size())
//                .forEach(i -> topResults.get(i).setPosition(i));

        return topResults;
    }

    /**
     * Returns QuerySpectrumId if it exists. Otherwise, returns a unique integer corresponding to the QueryFileIndex
     * and QuerySpectrumIndex
     *
     * @param searchResult search result
     * @return query ID
     */
    private static long getQueryId(SearchResultDTO searchResult) {
        if (searchResult.getQuerySpectrumId() != null && searchResult.getQuerySpectrumId() > 0)
            return searchResult.getQuerySpectrumId();

        // Cantor pairing function
        return searchResult.getQuerySpectrumIndex()
                + (searchResult.getQueryFileIndex() + searchResult.getQuerySpectrumIndex())
                * (searchResult.getQueryFileIndex() + searchResult.getQuerySpectrumIndex() + 1) / 2;
    }

    private String[] createHeader(SortedSet<String> propertyNames) {
        List<String> fields = Arrays.stream(ExportField.values())
                .map(field -> field.name)
                .collect(Collectors.toList());
        fields.addAll(propertyNames);
        return fields.toArray(new String[0]);
    }

    private String[] createRow(SortedSet<String> propertyNames,
                               Map<Long, List<SpectrumProperty>> spectrumIdToPropertiesMap,
                               SearchResultDTO searchResult) {

        List<String> values = Arrays.stream(ExportField.values())
                .map(field -> field.getter.apply(searchResult))
                .collect(Collectors.toList());

        List<SpectrumProperty> properties = spectrumIdToPropertiesMap.get(searchResult.getSpectrumId());
        for (String propertyName : propertyNames) {
            String value = null;
            if (properties != null) {
                for (SpectrumProperty property : properties) {
                    if (property.getName().equals(propertyName)) {
                        value = property.getValue();
                        break;
                    }
                }
            }
            values.add(value);
        }

        return values.toArray(new String[0]);
    }


    /**
     * Standard fields that we export in every CSV files
     */
    private enum ExportField {

        //        POSITION("Position", r -> Integer.toString(r.getPosition())),
        QUERY_FILE_ID("File", r -> r.getQueryFileIndex() != null ? Integer.toString(r.getQueryFileIndex() + 1) : null),
        QUERY_SPECTRUM_ID("Feature", r -> r.getQuerySpectrumIndex() != null ? Integer.toString(r.getQuerySpectrumIndex() + 1) : null),
        QUERY_EXTERNAL_ID("Signal ID", SearchResultDTO::getQueryExternalId),
        QUERY_NAME("Query", SearchResultDTO::getQuerySpectrumName),
        MATCH_NAME("Match", SearchResultDTO::getName),
        SCORE("Fragmentation Score", r -> r.getScore() != null ? Double.toString(r.getNISTScore()) : null),
        //        MASS_ERROR("Mass Error (Da)", r -> r.getMassError() != null ? Double.toString(r.getMassError()) : null),
        MASS_ERROR_PPM("Mass Error (PPM)", r -> formatDouble(r.getMassErrorPPM())),
        RET_TIME_ERROR("Ret Time Error", r -> formatDouble(r.getRetTimeError())),
        ONTOLOGY_LEVEL("Ontology Level", SearchResultDTO::getOntologyLevel);


        private final String name;
        private final Function<SearchResultDTO, String> getter;

        ExportField(String name, Function<SearchResultDTO, String> getter) {
            this.name = name;
            this.getter = getter;
        }

        private static String formatDouble(Double x) {
            if (x == null)
                return null;
            return String.format("%.4f", x);
        }
    }
}
