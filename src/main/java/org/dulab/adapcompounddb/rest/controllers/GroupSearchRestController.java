package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.services.GroupSearchService;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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
        final DataTableResponse response = spectrumMatchService.groupSearchSort(searchStr, start, length, column, sortDirection, matches);
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

}