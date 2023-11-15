package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.json.JsonObject;
import javax.naming.directory.SearchResult;
import javax.xml.crypto.Data;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.controllers.BaseController;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.utils.DataUtils;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.GROUP_SEARCH_ASYNC_ATTRIBUTE_NAME;

@RestController
public class GroupSearchRestController extends BaseController {

    public static final ObjectMapper mapper = new ObjectMapper();
    public static final List<SearchResultDTO> EMPTY_LIST = new ArrayList<>(0);
    private final SpectrumMatchService spectrumMatchService;
    private final GroupSearchService groupSearchService;

    private final SubmissionService submissionService;

    private final SpectrumService spectrumService;

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Autowired
    public GroupSearchRestController(final SpectrumMatchService spectrumMatchService, final GroupSearchService groupSearchService, final SubmissionService submissionService, final SpectrumService spectrumService) {
        this.spectrumMatchService = spectrumMatchService;
        this.groupSearchService = groupSearchService;
        this.submissionService = submissionService;
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = "/file/group_search/data.json", produces = "application/json")
    public String fileGroupSearchResults(@RequestParam("start") final Integer start,
        @RequestParam("length") final Integer length,
        @RequestParam("search") final String searchStr,
        @RequestParam("columnStr") final String columnStr, final HttpSession session)
        throws JsonProcessingException {

        List<SearchResultDTO> matches;

        Object sessionObject = session.getAttribute(ControllerUtils.GROUP_SEARCH_MATCHES);

        DataTableResponse response = new DataTableResponse();
        if (sessionObject != null) {

            @SuppressWarnings("unchecked") List<SearchResultDTO> sessionMatches = (List<SearchResultDTO>) sessionObject;

            //Avoid ConcurrentModificationException by make a copy for sorting
            matches = new ArrayList<>(sessionMatches);
            response = groupSearchSort(false, searchStr, start, length, matches, columnStr);


        }

        return mapper.writeValueAsString(response);
    }

    @GetMapping(value ="/getOntologyLevels")
    public List<String> getOntologyLevels(@RequestParam(value = "isSavedResultPage") Boolean isSavedResultPage,
                                          @RequestParam(value = "submissionId", required = false) Long submissionId,
                                          final HttpSession session){
        UserPrincipal user = this.getCurrentUserPrincipal();
        if(isSavedResultPage){
            if(user!= null) {
                List<Long> spectrumIds = getSpectrumIdsFromSubmission(submissionId);
                //get ontologylevels from spectrum match
                List<SpectrumMatch> spectrumMatches = spectrumMatchService.findAllSpectrumMatchByUserIdAndQuerySpectrums(user.getId(), spectrumIds);
                return spectrumMatches.stream().map(sm->sm.getOntologyLevel()).filter(Objects::nonNull)
                        .distinct().collect(Collectors.toList());
            }
            else
                return null;
        }
        else {
            List<SearchResultDTO> searchResultFromSession;
            Object sessionObject = session.getAttribute(
                    ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

            if (sessionObject == null)
                return null;
            else {
                searchResultFromSession = new ArrayList<>((List<SearchResultDTO>) sessionObject);
                return searchResultFromSession.stream().map(s -> s.getOntologyLevel()).filter(Objects::nonNull)
                        .distinct().collect(Collectors.toList());
            }
        }
    }
    @PostMapping(value = "/getMatches")
    public String getMatchesById(@RequestBody JsonNode jsonObj, final HttpSession session)
        throws JsonProcessingException {

        Object sessionObject =session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_FILTERED);
        if(sessionObject == null)
            return null;
        else{
            Integer spectrumIndex = jsonObj.get("querySpectrumIndex").asInt();
            String spectrumName = jsonObj.get("querySpectrumName").asText();
            List<SearchResultDTO> searchResultFromSession = new ArrayList<>((List<SearchResultDTO>) sessionObject);
            List<SearchResultDTO> matches = searchResultFromSession.stream()
                .filter(s -> s.getQuerySpectrumIndex().equals(spectrumIndex) && s.getQuerySpectrumName()
                        .equals(spectrumName)).collect(Collectors.toList());
            session.setAttribute(ControllerUtils.GROUP_SEARCH_MATCHES, matches);

            return mapper.writeValueAsString(matches);
        }


    }
    @PostMapping(value ="/getSpectrumsByName")
    public String getSpectrumsByName(@RequestBody JsonNode jsonObj, final HttpSession session) throws JsonProcessingException {

//        Object sessionObject =session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_FILTERED);
        Object sessionObject =session.getAttribute(ControllerUtils.SPECTRUM_DTO_LIST);
        if(sessionObject == null)
            return null;
        else {
            String spectrumName = jsonObj.get("querySpectrumName").asText();
            List<SpectrumDTO> spectrumDTOListFromSession = new ArrayList<>((List<SpectrumDTO>) sessionObject);

            List<SpectrumDTO> spectrumDTOList = spectrumDTOListFromSession.stream()
                .filter(s -> s.getName().equals(spectrumName)).collect(Collectors.toList());

            //spectrum List which is shown on 2nd table
            session.setAttribute(ControllerUtils.SPECTRUM_LIST, spectrumDTOList);

            return mapper.writeValueAsString(spectrumDTOList);
        }
    }
    @RequestMapping(value = "/distinct_spectra/data.json", produces = "application/json")
    public String distinctSpectraResult(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("search") final String searchStr,
            @RequestParam("columnStr") final String columnStr,
            @RequestParam("matchFilter") final Integer showMatchesOnly,
            @RequestParam("ontologyLevel") final String ontologyLevel,
            @RequestParam(value = "scoreThreshold", required = false) final Double scoreThreshold,
            @RequestParam(value = "massError", required = false) final Double massError,
            @RequestParam(value = "retTimeError", required = false) final Double retTimeError,
            @RequestParam("matchName") final String matchName,
            final HttpSession session) throws JsonProcessingException {

        List<SearchResultDTO> spectrumDtoList;
        Object sessionObject = session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        DataTableResponse response = new DataTableResponse();
        if (sessionObject != null) {
            List<SearchResultDTO> spectrumsFromSession = (List<SearchResultDTO>) sessionObject;
            spectrumDtoList = new ArrayList<>(spectrumsFromSession);
            //filter matches only
            if(showMatchesOnly ==1)
                spectrumDtoList = spectrumDtoList.stream().filter(s->s.getSpectrumId() != 0).collect(Collectors.toList());
            //filter by ontology level
            if(!ontologyLevel.isEmpty())
                spectrumDtoList = spectrumDtoList.stream().filter(s-> s.getOntologyLevel() != null).filter(s->s.getOntologyLevel().equals(ontologyLevel)).collect(
                Collectors.toList());
            //filter by score Threshold
            if(scoreThreshold != null)
                spectrumDtoList = spectrumDtoList.stream().filter(s-> s.getScore() != null).filter(s-> s.getScore() > scoreThreshold).collect(
                    Collectors.toList());
            //filter by massError
            if(massError != null)
                spectrumDtoList = spectrumDtoList.stream().filter(s-> s.getMassError() != null).filter(s-> s.getMassError() < massError).collect(
                    Collectors.toList());
            //filter by retTimeError
            if(retTimeError != null)
                spectrumDtoList = spectrumDtoList.stream().filter(s-> s.getRetTimeError() != null).filter(s-> s.getRetTimeError() < retTimeError).collect(
                    Collectors.toList());
            //filter by match name
            if(!matchName.isEmpty())
                spectrumDtoList = spectrumDtoList.stream().filter(s -> s.getName() != null).filter(s-> s.getName().contains(matchName)).collect(
                    Collectors.toList());

            session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_FILTERED, spectrumDtoList);
            //get the distinct spectra
            Set<String> distinctSpectraNames = new HashSet<>();
            spectrumDtoList = spectrumDtoList.stream().filter(
                s -> distinctSpectraNames.add(
                    s.getQuerySpectrumName())).collect(Collectors.toList());

            response = groupSearchSort(false, searchStr,  start, length, spectrumDtoList, columnStr);
        }
        return mapper.writeValueAsString(response);
    }
    @GetMapping(value = "/spectra/data.json", produces = "application/json")
    public String SpectraResult(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("search") final String searchStr,
            @RequestParam("columnStr") final String columnStr,
            final HttpSession session) throws JsonProcessingException {


        Object sessionObject = session.getAttribute(ControllerUtils.SPECTRUM_LIST);
        Object sessionGroupSearchResultFilteredObject =session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_FILTERED);
        DataTableResponse response = new DataTableResponse();

        List<SpectrumDTO> querySpectrumsFromSession = (List<SpectrumDTO>) sessionObject;
        List<SearchResultDTO> groupSearchResultFiltered = (List<SearchResultDTO>) sessionGroupSearchResultFilteredObject;
        List<SearchResultDTO> querySpectrums = querySpectrumsFromSession.stream()
                .filter(spectrum -> groupSearchResultFiltered.stream()
                        .anyMatch(filteredResult -> filteredResult.getQuerySpectrumIndex().equals(spectrum.getSpectrumIndex())))
                .map(spectrum ->{
                    SearchResultDTO searchResultDTO = new SearchResultDTO();
                    searchResultDTO.setQuerySpectrumName(spectrum.getName());
                    searchResultDTO.setQuerySpectrumIndex(spectrum.getSpectrumIndex());
                    searchResultDTO.setRetTime(spectrum.getRetentionTime());
                    searchResultDTO.setExternalId(spectrum.getExternalId());
                    return searchResultDTO;
                }).collect(Collectors.toList());

        response = groupSearchSort(false, searchStr,  start, length, querySpectrums, columnStr);

        return mapper.writeValueAsString(response);
    }
    
    @GetMapping(value = "/getSpectraForSavedResultPage")
    public String SpectraSavedResultPage(
        @RequestParam("start") final Integer start,
        @RequestParam("length") final Integer length,
        @RequestParam("search") final String searchStr,
        @RequestParam("columnStr") final String columnStr,
        @RequestParam("querySpectrumName") final String spectrumName,
        @RequestParam("matchFilter") final Integer showMatchesOnly,
        @RequestParam("ontologyLevel") final String ontologyLevel,
        @RequestParam(value = "scoreThreshold", required = false) final Double scoreThreshold,
        @RequestParam(value = "massError", required = false) final Double massError,
        @RequestParam(value = "retTimeError", required = false) final Double retTimeError,
        @RequestParam("matchName") final String matchName,
        final HttpSession session) throws JsonProcessingException {

        int matchIndex =0;
        DataTableResponse response = new DataTableResponse();

        List<SpectrumMatch> spectrumMatchList = spectrumMatchService.getMatchesByUserAndSpectrumName(this.getCurrentUserPrincipal().getId(), spectrumName, showMatchesOnly,
            ontologyLevel, scoreThreshold, massError, retTimeError, matchName);
        List<SearchResultDTO> searchResultDTOs = new ArrayList<>();
        for(SpectrumMatch sm : spectrumMatchList){
            SearchResultDTO result = MappingUtils.mapSpectrumMatchToSpectrumClusterView(sm,
                matchIndex++, null, null, null);
            result.setChromatographyTypeLabel(
                sm.getMatchSpectrum().getChromatographyType().getLabel());
            result.setRetTimeError(sm.getRetTimeError());
            result.setOntologyLevel(sm.getOntologyLevel());
            searchResultDTOs.add(result);
        }
        response = groupSearchSort(false, searchStr,  start, length, searchResultDTOs, columnStr);

        return mapper.writeValueAsString(response);
    }

    @RequestMapping(value = "/findMatchesSavedResultPage/data.json", produces = "application/json")
    public String findMatchesSavedResultPage(@RequestParam("start") final Integer start,
        @RequestParam("length") final Integer length,
        @RequestParam("search") final String searchStr,
        @RequestParam("columnStr") final String columnStr,
        @RequestParam("spectrumId") Long spectrumId,
        @RequestParam("matchId") Long matchId, final HttpSession session)
        throws JsonProcessingException {

        List<SearchResultDTO> matches = new ArrayList<>();
        int matchIndex = 0;
        DataTableResponse response = new DataTableResponse();

        List<SpectrumMatch> spectrumMatches = spectrumMatchService.findMatchesByUserIdAndQueryIdAndMatchId(this.getCurrentUserPrincipal().getId(), spectrumId, matchId);
        for (SpectrumMatch match : spectrumMatches) {
            SearchResultDTO searchResult = MappingUtils.mapSpectrumMatchToSpectrumClusterView(
                match, matchIndex++, null, null, null);
            searchResult.setChromatographyTypeLabel(match.getMatchSpectrum() != null ? match.getMatchSpectrum().getChromatographyType().getLabel() : null);
            searchResult.setOntologyLevel(match.getOntologyLevel());
            matches.add(searchResult);
        }
        response = groupSearchSort(false, searchStr, start, length, matches, columnStr);

        return mapper.writeValueAsString(response);
    }

    @RequestMapping(value = "/file/group_search_matches/{submissionId:\\d+}/data.json", produces = "application/json")
    public String groupSearchMatchesResults(
            @PathVariable("submissionId") long submissionId,
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("search") final String searchStr,
            @RequestParam("columnStr") final String columnStr,
            @RequestParam("matchFilter") final Integer showMatchesOnly,
            @RequestParam("ontologyLevel") final String ontologyLevel,
            @RequestParam(value = "scoreThreshold", required = false) final Double scoreThreshold,
            @RequestParam(value = "massError", required = false) final Double massError,
            @RequestParam(value = "retTimeError", required = false) final Double retTimeError,
            @RequestParam("matchName") final String matchName,
            final HttpSession session) throws JsonProcessingException {

        DataTableResponse response = new DataTableResponse();

        if (getCurrentUserPrincipal() != null) {

            List<Long> spectrumIds = getSpectrumIdsFromSubmission(submissionId);
            Page<String> distinctQuerySpectrum = spectrumMatchService.findAllDistinctSpectrumByUserIdAndQuerySpectrumsPageable
                (getCurrentUserPrincipal().getId(), spectrumIds, start, length, showMatchesOnly,
                    ontologyLevel, scoreThreshold, massError, retTimeError, matchName);

            List<SearchResultDTO> searchResultDTOList = new ArrayList<>();
            int position = 0;
            for(String query : distinctQuerySpectrum.getContent())
            {

                SearchResultDTO searchResult = new SearchResultDTO();
                searchResult.setQuerySpectrumName(query);
                searchResult.setPosition(position++);
                searchResultDTOList.add(searchResult);
            }

            response = groupSearchSort(true, searchStr, start, length, searchResultDTOList, columnStr);
            response.setRecordsTotal(distinctQuerySpectrum.getTotalElements());
            response.setRecordsFiltered(distinctQuerySpectrum.getTotalElements());
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

    @RequestMapping(value = "/group_search/status", produces = "application/json")
    public int fileGroupSearchStatus( HttpSession session) {
        Future<Void> asyncResult = (Future<Void>) session.getAttribute(GROUP_SEARCH_ASYNC_ATTRIBUTE_NAME);
        if (asyncResult != null && !asyncResult.isDone() && !asyncResult.isCancelled()) {
            return 1;
        }
        return 0;
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
    //helper method to get spectrum ids from a submsission
    private List<Long>  getSpectrumIdsFromSubmission(Long submissionId){
        Submission submission = submissionService.fetchSubmissionPartial(submissionId);

        List<File> files = submission.getFiles();
        List<Spectrum> spectrumList = new ArrayList<>();
        for (File file : files) {
            if (file != null && file.getSpectra() != null) {
                spectrumList.addAll(file.getSpectra());
            }
        }
        return spectrumList.stream().map(Spectrum::getId).collect(Collectors.toList());
    }

    private DataTableResponse groupSearchSort(boolean isPageResult, final String searchStr, final Integer start, final Integer length,
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
        DataTableResponse response = new DataTableResponse();
        if(!isPageResult) {
            for (int i = 0; i < spectrumList.size(); i++) {
                if (i < start || spectrumMatchList.size() >= length)
                    continue;
                spectrumMatchList.add(spectrumList.get(i));

            }

            response = new DataTableResponse(spectrumMatchList);
            response.setRecordsTotal((long) spectrumList.size());
            response.setRecordsFiltered((long) spectrumList.size());
        }
        else{
            response = new DataTableResponse(spectrumList);
        }

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
    private enum GroupSearchQueryTableColumnInformation {
        ID(0, "id"),
        QUERY_SPECTRUM(1, "querySpectrumName"),
        EXTERNAL_ID(2, "queryExternalId"),
        PRECURSOR_MZS(3, "queryPrecursorMzs"),
        RET_TIME(4, "queryRetTime");


        private int position;
        private String sortColumnName;

        GroupSearchQueryTableColumnInformation(final int position, final String sortColumnName) {
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
            for (final GroupSearchQueryTableColumnInformation groupSearchQueryTableColumnInformation : GroupSearchQueryTableColumnInformation.values()) {
                if (position == groupSearchQueryTableColumnInformation.getPosition()) {
                    columnName = groupSearchQueryTableColumnInformation.getSortColumnName();
                }
            }
            return columnName;
        }
    }
}