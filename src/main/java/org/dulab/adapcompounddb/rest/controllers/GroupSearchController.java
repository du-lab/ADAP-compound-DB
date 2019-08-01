package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.GroupSearchDTO;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@RestController
public class GroupSearchController {
    //TODO: remove spectrumSearchServiceMap
    private final SpectrumMatchService spectrumMatchService;

    @Autowired
    public GroupSearchController(final SpectrumMatchService spectrumMatchService) {

        this.spectrumMatchService = spectrumMatchService;
        //TODO: remove spectrumSearchServiceMap
    }

    @RequestMapping(value = "/file/group_search_results/data", produces = "application/json")
    public String fileGroupSearchResults(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column,
            @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr,
            final HttpSession session) throws JsonProcessingException {

        List<GroupSearchDTO> matches;
        List<GroupSearchDTO> matchesCopy;
        if (session.getAttribute("group_search_results") != null) {
            //TODO: change "group_search_results" to GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME
            matches = (List<GroupSearchDTO>) session.getAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

            //Avoid ConcurrentModificationException by make a copy for sorting
            matchesCopy = new ArrayList<>(matches);
        } else {
            //TODO: Create a static variable List<GroupSearchDTO> EMPTY_LIST and use it here
            matches = ControllerUtils.EMPTY_LIST;

            //Avoid ConcurrentModificationException by make a copy for sorting
            matchesCopy = new ArrayList<>(matches);
        }

        final DataTableResponse response = spectrumMatchService.groupSearchSort(searchStr, start, length, column, sortDirection, matchesCopy);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final String jsonString = mapper.writeValueAsString(response);
        return jsonString;
    }

}