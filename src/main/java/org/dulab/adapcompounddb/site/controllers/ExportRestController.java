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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class ExportRestController {

    private static final Logger LOGGER = LogManager.getLogger(ExportRestController.class);

    private final SpectrumService spectrumService;

    @Autowired
    public ExportRestController(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }


    @RequestMapping(value = "/export/session/{attribute}/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void export(@PathVariable("attribute") String attributeName,
                       HttpSession session, HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");

        Object attribute = session.getAttribute(attributeName);
        if (!(attribute instanceof List)) {
            LOGGER.warn(String.format("Attribute %s is not of type List", attributeName));
            return;
        }

        List<?> list = (List<?>) attribute;
        long[] matchIds = list.stream()
                .filter(o -> o instanceof SearchResultDTO)
                .map(o -> (SearchResultDTO) o)
                .mapToLong(SearchResultDTO::getSpectrumId)
                .toArray();

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
            for (Object element : (List<?>) attribute) {
                if (!(element instanceof SearchResultDTO)) {
                    LOGGER.warn(String.format("Element of %s is not of type SearchResultDTO", attributeName));
                    return;
                }

                csvWriter.writeNext(createRow(propertyNames, spectrumIdToPropertiesMap, (SearchResultDTO) element));
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

    /**
     * Standard fields that we export in every CSV files
     */
    private enum ExportField {

        POSITION("Position", r -> Integer.toString(r.getPosition())),
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
