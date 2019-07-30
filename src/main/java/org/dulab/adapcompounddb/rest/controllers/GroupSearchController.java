package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.GroupSearchDTO;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.SearchController;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class GroupSearchController {

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;
    private final SpectrumMatchService spectrumMatchService;

    @Autowired
    public GroupSearchController(final SpectrumMatchService spectrumMatchService,
                                 @Qualifier("spectrumSearchServiceGCImpl") final SpectrumSearchService gcSpectrumSearchService,
                                 @Qualifier("spectrumSearchServiceLCImpl") final SpectrumSearchService lcSpectrumSearchService) {

        this.spectrumMatchService = spectrumMatchService;
        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, lcSpectrumSearchService);
    }

    @RequestMapping(value = "/file/group_search_results/data", produces = "application/json")
    public String fileGroupSearchResults(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column,
            @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr,
            final HttpSession session, final SearchController.SearchForm form) throws JsonProcessingException {

        // Assume scoreThreshold = 0.75
        // mzTolerance = 0.01

        /*
        1. Match all spectra from the session to the library and get a list of SpectrumMatch
        2. Sort the List<SpectrumMatch> based on the `column` and `sortDirection` parameters.
        3. Based on "start", "length", return only the required results
        4. Return json-string of DataTableResponse with the matching results
         */

        // 1.
        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }

        List<GroupSearchDTO> matches;
        if (session.getAttribute("group_search_results") != null) {
            matches = (List<GroupSearchDTO>) session.getAttribute("group_search_results");
        } else {
            matches = new ArrayList<>();
        }

        final DataTableResponse response = spectrumMatchService.groupSearchSort(searchStr, start,
                length, column, sortDirection, matches);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final String jsonString = mapper.writeValueAsString(response);
        System.out.println(jsonString);
        return jsonString;
    }
}