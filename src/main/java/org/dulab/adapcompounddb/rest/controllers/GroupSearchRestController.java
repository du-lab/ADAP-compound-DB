package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Serializable;
import javax.json.JsonObject;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.BaseController;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.utils.DataUtils;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class GroupSearchRestController extends BaseController {

    public static final ObjectMapper mapper = new ObjectMapper();
    public static final List<SearchResultDTO> EMPTY_LIST = new ArrayList<>(0);
    private final SpectrumMatchService spectrumMatchService;
    private final GroupSearchService groupSearchService;

    private final SubmissionService submissionService;

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Autowired
    public GroupSearchRestController(final SpectrumMatchService spectrumMatchService, final GroupSearchService groupSearchService, final SubmissionService submissionService) {
        this.spectrumMatchService = spectrumMatchService;
        this.groupSearchService = groupSearchService;
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/file/group_search/data.json", produces = "application/json")
    public String fileGroupSearchResults(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("search") final String searchStr,
            @RequestParam("columnStr") final String columnStr,
            final HttpSession session) throws JsonProcessingException {

        List<SearchResultDTO> matches;

        Object sessionObject = session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        Page<SpectrumMatch> spectrumMatchPage;
        DataTableResponse response = new DataTableResponse();
        if (sessionObject != null) {

            @SuppressWarnings("unchecked")
            List<SearchResultDTO> sessionMatches = (List<SearchResultDTO>) sessionObject;

            //Avoid ConcurrentModificationException by make a copy for sorting
            matches = new ArrayList<>(sessionMatches);
            response = groupSearchSort(true, searchStr, start, length, matches, columnStr);


        }

        return mapper.writeValueAsString(response);
    }
    @PostMapping(value ="/getSpectrumsByName")
    public String getSpectrumsByName(@RequestBody JsonNode jsonObj, final HttpSession session) throws JsonProcessingException {

        String spectrumName = jsonObj.get("name").asText();
        List<SpectrumDTO> spectraFromSession = (List<SpectrumDTO>) session.getAttribute("all_spectra");
        DataTableResponse response = new DataTableResponse();

        List<SpectrumDTO> spectrumDTOList = spectraFromSession.stream().filter(s->s.getName().equals(spectrumName)).collect(
            Collectors.toList());

        session.setAttribute("spectrum_list", spectrumDTOList);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(spectrumDTOList);
    }
    @GetMapping(value = "/distinct_spectra/data.json", produces = "application/json")
    public String distinctSpectraResult(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("column")  Integer column,
            @RequestParam("sortDirection") String sortDirection,
            final HttpSession session) throws JsonProcessingException {


        Object sessionObject = session.getAttribute("distinct_spectra");
        DataTableResponse response = new DataTableResponse();

        List<SpectrumDTO> querySpectrums = (List<SpectrumDTO>) sessionObject;
        response = spectrumSort(querySpectrums, start, length, column, sortDirection);

        return mapper.writeValueAsString(response);
    }
    @GetMapping(value = "/spectra/data.json", produces = "application/json")
    public String SpectraResult(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("column")  Integer column,
            @RequestParam("sortDirection") String sortDirection,
            final HttpSession session) throws JsonProcessingException {


        Object sessionObject = session.getAttribute("spectrum_list");
        DataTableResponse response = new DataTableResponse();

        List<SpectrumDTO> querySpectrums = (List<SpectrumDTO>) sessionObject;
        response = spectrumSort(querySpectrums, start, length, column, sortDirection);

        return mapper.writeValueAsString(response);
    }
    public DataTableResponse spectrumSort(List<SpectrumDTO> querySpectrums, Integer start,
                                             Integer length, Integer column, String sortDirection){
        final List<SpectrumDTO> querySpectrumList = new ArrayList<>();
        for (int i = 0; i < querySpectrums.size(); i++) {
            if (i < start || querySpectrumList.size() >= length)
                continue;

            querySpectrumList.add(querySpectrums.get(i));

        }
        DataTableResponse response = new DataTableResponse(querySpectrumList);
        response.setRecordsFiltered((long) querySpectrums.size());
        response.setRecordsTotal((long) querySpectrums.size());

        return response;
    }
    @RequestMapping(value = "/file/group_search_matches/{submissionId:\\d+}/data.json", produces = "application/json")
    public String groupSearchMatchesResults(
            @PathVariable("submissionId") long submissionId,
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("search") final String searchStr,
            @RequestParam("columnStr") final String columnStr,
            final HttpSession session) throws JsonProcessingException {

        Page<SpectrumMatch> spectrumMatches;
        List<SearchResultDTO> matches = new ArrayList<>();
        DataTableResponse response = new DataTableResponse();

        if (getCurrentUserPrincipal() != null) {
            int matchIndex = 0;

            Submission submission = submissionService.fetchSubmissionPartial(submissionId);

            List<File> files = submission.getFiles();
            List<Spectrum> spectrumList = new ArrayList<>();
            for (File file : files) {
                if (file != null && file.getSpectra() != null) {
                    spectrumList.addAll(file.getSpectra());
                }
            }
            List<Long> spectrumIds = spectrumList.stream().map(Spectrum::getId).collect(Collectors.toList());

            String[] columns = columnStr.split("[-,]");
            Integer column = Integer.parseInt(columns[0]);
            String sortDirection = columns[1];

            //get column name that is sorted
            String sortColumn = GroupSearchColumnInformation.getColumnNameFromPosition(column);

            spectrumMatches = spectrumMatchService.findAllSpectrumMatchByUserIdAndQuerySpectrumsPageable
                    (getCurrentUserPrincipal().getId(), spectrumIds, start, length, sortColumn, sortDirection);

            for (SpectrumMatch match : spectrumMatches.getContent()) {
                SearchResultDTO searchResult = MappingUtils.mapSpectrumMatchToSpectrumClusterView(
                        match, matchIndex++, null, null, null);
                searchResult.setChromatographyTypeLabel(match.getMatchSpectrum() != null ? match.getMatchSpectrum().getChromatographyType().getLabel() : null);
                searchResult.setOntologyLevel(match.getOntologyLevel());

                matches.add(searchResult);
            }
            response = groupSearchSort(false, searchStr, start, length, matches, columnStr);
            response.setRecordsTotal(spectrumMatches.getTotalElements());
            response.setRecordsFiltered(spectrumMatches.getTotalElements());
        }

        return mapper.writeValueAsString(response);
    }


    @RequestMapping(value = "/group_search/progress", produces = "application/json")
    public int fileGroupSearchProgress( HttpSession session) {
        Object progressObject = session.getAttribute(ControllerUtils.GROUP_SEARCH_PROGRESS_ATTRIBUTE_NAME);
        if (!(progressObject instanceof Float))
            return 0;

        // Return json-string containing a number between 0 and 100.
        float progress = (Float) progressObject;
        return Math.round(100 * progress);
    }

    @RequestMapping(value = "/submission/group_search/{submissionId:\\d+}/progress", produces = "application/json")
    public int groupSearchProgress(@PathVariable("submissionId") long submissionId, HttpSession session) {
        Object progressObject = session.getAttribute(ControllerUtils.GROUP_SEARCH_PROGRESS_ATTRIBUTE_NAME);
        if (!(progressObject instanceof Float))
            return 0;

        // Return json-string containing a number between 0 and 100.
        float progress = (Float) progressObject;
        return Math.round(100 * progress);
    }


    private DataTableResponse groupSearchSort(boolean groupSearchAsync, final String searchStr, final Integer start, final Integer length,
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
            if(groupSearchAsync) {
                if (i < start || spectrumMatchList.size() >= length)
                    continue;
            }
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
                case "isotopicSimilarity":
                    comparator = getComparator(SearchResultDTO::getIsotopicSimilarity, sortDirection);
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
                    comparator = getComparator(SearchResultDTO::getOntologyPriority, sortDirection);
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
        ISOTOPIC_SIMILARITY(10, "isotopicSimilarity"),
        AVERAGE_SIGNIFICANCE(11, "averageSignificance"),
        MINIMUM_SIGNIFICANCE(12, "minimumSignificance"),
        MAXIMUM_SIGNIFICANCE(13, "maximumSignificance"),
        ONTOLOGY_LEVEL(14, "ontologyLevel"),
        CHROMATOGRAPHY_TYPE(15, "chromatographyType");

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