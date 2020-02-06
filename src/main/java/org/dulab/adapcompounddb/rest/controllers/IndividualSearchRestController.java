package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.controllers.utils.PaginationUtils;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class IndividualSearchRestController {

    public static final List<ClusterDTO> EMPTY_LIST = new ArrayList<>(0);

    private final SpectrumMatchService spectrumMatchService;

    public IndividualSearchRestController(SpectrumMatchService spectrumMatchService) {
        this.spectrumMatchService = spectrumMatchService;
    }

    @RequestMapping(value = "/rest/individual_search/json", produces = "application/json")
    public String get(
            @RequestParam("start") int start,
            @RequestParam("length") int length,
            @RequestParam("column") int column,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam("search") String search,
            HttpSession session) throws JsonProcessingException {

        @SuppressWarnings("unchecked")
        List<ClusterDTO> matches =
                (List<ClusterDTO>) session.getAttribute(ControllerUtils.INDIVIDUAL_SEARCH_RESULTS_ATTRIBUTE_NAME);

        if (matches == null)
            matches = EMPTY_LIST;

        List<ClusterDTO> page = PaginationUtils.getPage(matches, start, length);

        DataTableResponse response = new DataTableResponse(page);
        response.setRecordsTotal((long) matches.size());
        response.setRecordsFiltered((long) matches.size());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(response);
    }
}
