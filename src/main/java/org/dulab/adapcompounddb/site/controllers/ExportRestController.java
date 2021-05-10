package org.dulab.adapcompounddb.site.controllers;

import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class ExportRestController {

    private static final Logger LOGGER = LogManager.getLogger(ExportRestController.class);

    private final SpectrumService spectrumService;

    @Autowired
    public ExportRestController(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }


    @RequestMapping(value = "/export/session/{attribute}/simple_csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void simpleExport(@PathVariable("attribute") String attributeName,
                             HttpSession session, HttpServletResponse response) {
        export(attributeName, session, response, false);
    }

    @RequestMapping(value = "/export/session/{attribute}/advanced_csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void advancedExport(@PathVariable("attribute") String attributeName,
                               HttpSession session, HttpServletResponse response) {
        export(attributeName, session, response, true);
    }

    /**
     * Exports a list stored in the current session into CSV file
     *
     * @param attributeName session attribute name that is used to store the list
     * @param session       current session
     * @param response      HTTP Servlet response
     * @param advanced      if true, all matched are exported. Otherwise, only the best match is exported for each feature
     */
    private void export(String attributeName, HttpSession session, HttpServletResponse response, boolean advanced) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.csv\"", advanced ? "advanced_export" : "simple_export"));

        Object attribute = session.getAttribute(attributeName);
        if (!(attribute instanceof List)) {
            LOGGER.warn(String.format("Attribute %s is not of type List", attributeName));
            return;
        }

        List<SearchResultDTO> searchResults = ((List<?>) attribute).stream()
                .filter(object -> object instanceof SearchResultDTO)
                .map(object -> (SearchResultDTO) object)
                .collect(Collectors.toList());

        if (!advanced)
            searchResults = selectTopResults(searchResults);

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

        try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {

            csvWriter.writeNext(createHeader(propertyNames));
            for (SearchResultDTO searchResult : searchResults) {
                csvWriter.writeNext(createRow(propertyNames, spectrumIdToPropertiesMap, searchResult));
            }

        } catch (IOException e) {
            LOGGER.warn("Error while writing to a CSV file: " + e.getMessage(), e);
        }
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

    private List<SearchResultDTO> selectTopResults(List<SearchResultDTO> searchResults) {

        long[] queryIds = searchResults.stream()
                .mapToLong(ExportRestController::getQueryId)
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


    /**
     * Standard fields that we export in every CSV files
     */
    private enum ExportField {

        //        POSITION("Position", r -> Integer.toString(r.getPosition())),
        QUERY_FILE_ID("File", r -> r.getQueryFileIndex() != null ? Integer.toString(r.getQueryFileIndex() + 1) : null),
        QUERY_SPECTRUM_ID("Feature", r -> r.getQuerySpectrumIndex() != null ? Integer.toString(r.getQuerySpectrumIndex() + 1) : null),
        QUERY_EXTERNAL_ID("ID", SearchResultDTO::getQueryExternalId),
        QUERY_NAME("Query", SearchResultDTO::getQuerySpectrumName),
        MATCH_NAME("Match", SearchResultDTO::getName),
        SCORE("Score", r -> r.getScore() != null ? Double.toString(r.getNISTScore()) : null),
        MASS_ERROR("Mass Error (Da)", r -> r.getMassError() != null ? Double.toString(r.getMassError()) : null),
        MASS_ERROR_PPM("Mass Error (PPM)", r -> r.getMassErrorPPM() != null ? Double.toString(r.getMassErrorPPM()) : null),
        RET_TIME_ERROR("Ret Time Error", r -> r.getRetTimeError() != null ? Double.toString(r.getRetTimeError()) : null),
        ONTOLOGY_LEVEL("Ontology Level", SearchResultDTO::getOntologyLevel);


        private final String name;
        private final Function<SearchResultDTO, String> getter;

        ExportField(String name, Function<SearchResultDTO, String> getter) {
            this.name = name;
            this.getter = getter;
        }
    }

}
