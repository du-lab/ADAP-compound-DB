package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class GroupSearchRestController {

    public static final ObjectMapper mapper = new ObjectMapper();
    public static final List<SearchResultDTO> EMPTY_LIST = new ArrayList<>(0);
    private final SpectrumMatchService spectrumMatchService;
    private final GroupSearchService groupSearchService;

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Autowired
    public GroupSearchRestController(final SpectrumMatchService spectrumMatchService, final GroupSearchService groupSearchService) {
        this.spectrumMatchService = spectrumMatchService;
        this.groupSearchService = groupSearchService;
    }

    @RequestMapping(value = "/file/group_search/data", produces = "application/json")
    public String fileGroupSearchResults(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("search") final String searchStr,
            @RequestParam("columnStr") final String columnStr,
            final HttpSession session) throws JsonProcessingException {

        List<SearchResultDTO> matches;

        Object sessionObject = session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        if (sessionObject != null) {

            @SuppressWarnings("unchecked")
            List<SearchResultDTO> sessionMatches = (List<SearchResultDTO>) sessionObject;

            //Avoid ConcurrentModificationException by make a copy for sorting
            matches = new ArrayList<>(sessionMatches);

        } else {
            matches = new ArrayList<>(EMPTY_LIST);
        }
        final DataTableResponse response = groupSearchSort(searchStr, start, length, matches, columnStr);
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(response);
    }

    @RequestMapping(value = {"/file/group_search/progress", "/submission/*/group_search/progress}"},
            produces = "application/json")
    public int fileGroupSearchProgress(HttpSession session) {
        Object progressObject = session.getAttribute(GroupSearchService.groupSearchProgress);
        if (!(progressObject instanceof Float))
            return 0;

        // Return json-string containing a number between 0 and 100.
        float progress = (Float) progressObject;
        return Math.round(100 * progress);
    }

//    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/progress", produces = "application/json")
//    public int submissionGroupSearchProgress(@PathVariable("submissionId") final long submissionId, HttpSession session) {
//        Object progressObject = session.getAttribute(GroupSearchService.groupSearchProgress);
//        if (!(progressObject instanceof Float))
//            return 0;
//
//        // Return json-string containing a number between 0 and 100.
//        float progress = (Float) progressObject;
//        return Math.round(100 * progress);
//    }

    private DataTableResponse groupSearchSort(final String searchStr, final Integer start, final Integer length,
                                              List<SearchResultDTO> spectrumList, final String columnStr) {

        if (searchStr != null && searchStr.trim().length() > 0)
            spectrumList = spectrumList.stream()
                    .filter(s -> (s.getQuerySpectrumName() != null && s.getQuerySpectrumName().contains(searchStr))
                            || (s.getName() != null && s.getName().contains(searchStr)))
                    .collect(Collectors.toList());

        String[] columns = columnStr.split("[-,]");
        List<String> columnNumbersAndDirections = Arrays.asList(columns);

        List<String> columnNumbers = new ArrayList<>();
        List<String> columnDirections = new ArrayList<>();

        for (int i = 0; i < columnNumbersAndDirections.size(); i++) {
            if (i % 2 == 0) {
                columnNumbers.add(columnNumbersAndDirections.get(i));
            } else {
                columnDirections.add(columnNumbersAndDirections.get(i));
            }
        }

        if (columnNumbers.size() == columnDirections.size() && !columnNumbers.isEmpty() || !columnDirections.isEmpty()) {
            Comparator<SearchResultDTO> multiColumnComparator = null;
            for (int i = 0; i < columnNumbers.size(); i++) {

                int columnNum = Integer.parseInt(columnNumbers.get(i));
                String columnDir = columnDirections.get(i);

                String sortColumn = GroupSearchColumnInformation.getColumnNameFromPosition(columnNum);

                Comparator<SearchResultDTO> comparator = comparingColumns(sortColumn, columnDir);

                if (multiColumnComparator == null) {
                    multiColumnComparator = comparator;
                } else {
                    multiColumnComparator = multiColumnComparator.thenComparing(comparator);
                }
            }

            spectrumList.sort(multiColumnComparator);
        } else {
            throw new IllegalStateException("Wrong sorting parameters");
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

    private Comparator<SearchResultDTO> comparingColumns(String column, String sortDirection) {
        Comparator<SearchResultDTO> comparator = null;

        if (column != null) {
            switch (column) {
                case "id":
                    comparator = getComparator(SearchResultDTO::getPosition, sortDirection);
                    break;
                case "querySpectrumName":
                    comparator = getComparator(SearchResultDTO::getQuerySpectrumName, sortDirection);
                    break;
                case "consensusSpectrumName":
                    comparator = getComparator(SearchResultDTO::getName, sortDirection);
                    break;
                case "mass":
                    comparator = getComparator(SearchResultDTO::getMass, sortDirection);
                    break;
                case "size":
                    comparator = getComparator(SearchResultDTO::getSize, sortDirection);
                    break;
                case "diameter":
                    comparator = getComparator(SearchResultDTO::getScore, sortDirection);
                    break;
                case "massError":
                    comparator = getComparator(SearchResultDTO::getMassError, sortDirection);
                    break;
                case "massErrorPPM":
                    comparator = getComparator(SearchResultDTO::getMassErrorPPM, sortDirection);
                    break;
                case "retTimeError":
                    comparator = getComparator(SearchResultDTO::getRetTimeError, sortDirection);
                    break;
                case "retIndexError":
                    comparator = getComparator(SearchResultDTO::getRetIndexError, sortDirection);
                    break;
                case "averageSignificance":
                    comparator = getComparator(SearchResultDTO::getAveSignificance, sortDirection);
                    break;
                case "minimumSignificance":
                    comparator = getComparator(SearchResultDTO::getMinSignificance, sortDirection);
                    break;
                case "maximumSignificance":
                    comparator = getComparator(SearchResultDTO::getMaxSignificance, sortDirection);
                    break;
                case "ontologyLevel":
                    comparator = getComparator(SearchResultDTO::getOntologyLevel, sortDirection);
                    break;
                case "chromatographyType":
                    comparator = getComparator(SearchResultDTO::getChromatographyTypeLabel, sortDirection);
                    break;
            }
        }
        return comparator;
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
        MASS(3, "mass"),
        COUNT(4, "size"),
        SCORE(5, "diameter"),
        MASS_ERROR(6, "massError"),
        MASS_ERROR_PPM(7, "massErrorPPM"),
        RET_TIME_ERROR(8, "retTimeError"),
        RET_INDEX_ERROR(9, "retIndexError"),
        AVERAGE_SIGNIFICANCE(10, "averageSignificance"),
        MINIMUM_SIGNIFICANCE(11, "minimumSignificance"),
        MAXIMUM_SIGNIFICANCE(12, "maximumSignificance"),
        ONTOLOGY_LEVEL(13, "ontologyLevel"),
        CHROMATOGRAPHY_TYPE(14, "chromatographyType");

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