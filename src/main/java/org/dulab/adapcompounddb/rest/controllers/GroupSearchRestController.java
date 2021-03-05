package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class GroupSearchRestController {

    public static final List<SearchResultDTO> EMPTY_LIST = new ArrayList<>(0);
    private final SpectrumMatchService spectrumMatchService;
    private final GroupSearchService groupSearchService;

    @Autowired
    public GroupSearchRestController(final SpectrumMatchService spectrumMatchService, final GroupSearchService groupSearchService) {
        this.spectrumMatchService = spectrumMatchService;
        this.groupSearchService = groupSearchService;
    }

    @RequestMapping(value = "/file/group_search/data", produces = "application/json")
    public String fileGroupSearchResults(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column,
            @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr,
            final HttpSession session) throws JsonProcessingException {

        List<SearchResultDTO> matches;

        if (session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME) != null) {

            @SuppressWarnings("unchecked")
            List<SearchResultDTO> sessionMatches =
                    (List<SearchResultDTO>) session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

            //Avoid ConcurrentModificationException by make a copy for sorting
            matches = new ArrayList<>(sessionMatches);

        } else {
            matches = new ArrayList<>(EMPTY_LIST);
        }
        final DataTableResponse response = groupSearchSort(searchStr, start, length, column, sortDirection, matches);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final String jsonString = mapper.writeValueAsString(response);
        return jsonString;
    }

    @RequestMapping(value = "/file/group_search/progress", produces = "application/json")
    public int fileGroupSearchProgress() {
        // Return json-string containing a number between 0 and 100.
        return Math.round(100 * groupSearchService.getProgress());
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/progress", produces = "application/json")
    public int submissionGroupSearchProgress(@PathVariable("submissionId") final long submissionId) {
        // Return json-string containing a number between 0 and 100.
        return Math.round(100 * groupSearchService.getProgress());
    }


    private DataTableResponse groupSearchSort(final String searchStr, final Integer start, final Integer length,
                                              final Integer column, final String sortDirection,
                                              List<SearchResultDTO> spectrumList) {

        if (searchStr != null && searchStr.trim().length() > 0)
            spectrumList = spectrumList.stream()
                    .filter(s -> (s.getQuerySpectrumName() != null && s.getQuerySpectrumName().contains(searchStr))
                            || (s.getName() != null && s.getName().contains(searchStr)))
                    .collect(Collectors.toList());

        String sortColumn = GroupSearchColumnInformation.getColumnNameFromPosition(column);

        // sorting each column
        if (sortColumn != null) {
            switch (sortColumn) {
                case "id":
                    spectrumList.sort(getComparator(SearchResultDTO::getPosition, sortDirection));
                    break;
                case "querySpectrumName":
                    spectrumList.sort(getComparator(SearchResultDTO::getQuerySpectrumName, sortDirection));
                    break;
                case "consensusSpectrumName":
                    spectrumList.sort(getComparator(SearchResultDTO::getName, sortDirection));
                    break;
                case "molecularWeight":
                    spectrumList.sort(getComparator(SearchResultDTO::getMolecularWeight, sortDirection));
                    break;
                case "size":
                    spectrumList.sort(getComparator(SearchResultDTO::getSize, sortDirection));
                    break;
                case "diameter":
                    spectrumList.sort(getComparator(SearchResultDTO::getScore, sortDirection));
                    break;
                case "massError":
                    spectrumList.sort(getComparator(SearchResultDTO::getMassError, sortDirection));
                    break;
                case "retTimeError":
                    spectrumList.sort(getComparator(SearchResultDTO::getRetTimeError, sortDirection));
                    break;
                case "averageSignificance":
                    spectrumList.sort(getComparator(SearchResultDTO::getAveSignificance, sortDirection));
                    break;
                case "minimumSignificance":
                    spectrumList.sort(getComparator(SearchResultDTO::getMinSignificance, sortDirection));
                    break;
                case "maximumSignificance":
                    spectrumList.sort(getComparator(SearchResultDTO::getMaxSignificance, sortDirection));
                    break;
                case "ontologyLevel":
                    spectrumList.sort(getComparator(SearchResultDTO::getOntologyLevel, sortDirection));
                    break;
                case "chromatographyType":
                    spectrumList.sort(getComparator(SearchResultDTO::getChromatographyTypeLabel, sortDirection));
                    break;
            }
        }

        final List<SearchResultDTO> spectrumMatchList = new ArrayList<>();
        for (int i = 0; i < spectrumList.size(); i++) {

            if (i < start || spectrumMatchList.size() >= length)
                continue;
            spectrumMatchList.add(spectrumList.get(i));

        }

        DataTableResponse response = new DataTableResponse(spectrumMatchList);
        response.setRecordsTotal((long) spectrumList.size());
        response.setRecordsFiltered((long) spectrumList.size());

        return response;
    }

    // function for sorting the column

    private <T extends Comparable> Comparator<SearchResultDTO> getComparator(
            Function<SearchResultDTO, T> function, String sortDirection) {

        return (o1, o2) -> {

            if (function.apply(o1) == null) {
                return (function.apply(o2) == null) ? 0 : 1;
            }
            if (function.apply(o2) == null) {
                return -1;
            }

            @SuppressWarnings("unchecked")
            int comparison = function.apply(o2).compareTo(function.apply(o1));

            if (sortDirection.equalsIgnoreCase("asc")) {
                return comparison;
            } else {
                return -comparison;
            }
        };
    }


    private enum GroupSearchColumnInformation {
        ID(0, "id"), QUERY_SPECTRUM(1, "querySpectrumName"),
        MATCH_SPECTRUM(2, "consensusSpectrumName"),
        MOLECULAR_WEIGHT(3, "molecularWeight"),
        COUNT(4, "size"),
        SCORE(5, "diameter"),
        MASS_ERROR(6, "massError"),
        RET_TIME_ERROR(7, "retTimeError"),
        AVERAGE_SIGNIFICANCE(8, "averageSignificance"),
        MINIMUM_SIGNIFICANCE(9, "minimumSignificance"),
        MAXIMUM_SIGNIFICANCE(10, "maximumSignificance"),
        ONTOLOGY_LEVEL(11, "ontologyLevel"),
        CHROMATOGRAPHY_TYPE(12, "chromatographyType");

        private int position;
        private String sortColumnName;

        GroupSearchColumnInformation(final int position, final String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        public static String getColumnNameFromPosition(final int position) {
            String columnName = null;
            for (final GroupSearchColumnInformation groupSearchColumnInformation : GroupSearchColumnInformation.values()) {
                if (position == groupSearchColumnInformation.getPosition()) {
                    columnName = groupSearchColumnInformation.getSortColumnName();
                }
            }
            return columnName;
        }
    }
}