package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.site.controllers.BaseController;
import org.dulab.adapcompounddb.site.controllers.utils.ConversionsUtils;
import org.dulab.adapcompounddb.site.controllers.utils.PaginationUtils;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.search.IndividualSearchService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class IndividualSearchRestController extends BaseController {

    private final SpectrumMatchService spectrumMatchService;
    private final IndividualSearchService spectrumSearchService;

    public IndividualSearchRestController(SpectrumMatchService spectrumMatchService,
                                          IndividualSearchService gcSpectrumSearchService) {  // @Qualifier("spectrumSearchServiceImpl")
        this.spectrumMatchService = spectrumMatchService;
        this.spectrumSearchService = gcSpectrumSearchService;
    }

    @RequestMapping(value = "/rest/individual_search/json", produces = "application/json")
    public String get(
            @RequestParam("start") int start,
            @RequestParam("length") int length,
            @RequestParam("column") int column,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam("search") String search,
            @RequestParam("queryJson") String queryJson,
            @RequestParam("scoreThreshold") double scoreThreshold,
            @RequestParam("mzTolerance") double mzTolerance,
            @RequestParam("species") String species,
            @RequestParam("source") String source,
            @RequestParam("disease") String disease,
            HttpSession session) throws JsonProcessingException {

        List<Peak> queryPeaks = ConversionsUtils.jsonToPeaks(queryJson);

        Spectrum querySpectrum = new Spectrum();
        querySpectrum.setPeaks(queryPeaks);

        SearchParameters parameters = new SearchParameters();
        parameters.setScoreThreshold(scoreThreshold / 1000.0);
        parameters.setMzTolerance(mzTolerance);
        parameters.setSpecies(species);
        parameters.setSource(source);
        parameters.setDisease(disease);

        List<SearchResultDTO> clusters = spectrumSearchService.searchConsensusSpectra(this.getCurrentUserPrincipal(),
                querySpectrum, parameters, true);

        List<SearchResultDTO> page = PaginationUtils.getPage(clusters, start, length, column, sortDirection);

        DataTableResponse response = new DataTableResponse(page);
        response.setRecordsTotal((long) clusters.size());
        response.setRecordsFiltered((long) clusters.size());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(response);
    }


}
