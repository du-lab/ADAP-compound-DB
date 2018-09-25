package org.dulab.adapcompounddb.rest.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
@RequestMapping("/spectrum")
public class SpectrumRestController {

    private final SpectrumService spectrumService;

    private final SpectrumMatchService spectrumMatchService;

    @Autowired
    public SpectrumRestController(final SpectrumService spectrumService, final SpectrumMatchService spectrumMatchService) {
        this.spectrumService = spectrumService;
        this.spectrumMatchService = spectrumMatchService;
    }

    @RequestMapping(value = "/findSpectrumBySubmissionId", produces = "application/json")
    public String findSpectrumBySubmissionId(@RequestParam("submissionId") final Long submissionId,
            @RequestParam("start") final Integer start, @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column, @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr, final HttpServletRequest request, final HttpSession session)
                    throws JsonProcessingException {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        DataTableResponse response = null;

        if(submissionId > 0 ) {
            response = spectrumService.findSpectrumBySubmissionId(submissionId, searchStr, start,
                    length, column, sortDirection);
        } else {
            response = spectrumService.processPagination(Submission.from(session), searchStr, start, length, column, sortDirection);
        }

        final String jsonString = objectMapper.writeValueAsString(response);
        return jsonString;
    }

    @RequestMapping(value = "/updateReferenceOfAllSpectraOfSubmission", produces = "application/json")
    public Boolean updateReferenceOfAllSpectraOfSubmission(@RequestParam("submissionId") final Long submissionId,
            @RequestParam("value") final boolean value) throws JsonProcessingException {
        return spectrumService.updateReferenceOfAllSpectraOfSubmission(submissionId, value);
    }

    @RequestMapping(value = "/findClusters", produces = "application/json")
    public String findClusters(@RequestParam("start") final Integer start, @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column, @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr, final HttpServletRequest request)
                    throws JsonProcessingException {

        /*final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        objectMapper.map(spectrumMatchService.getAllClusters(), SpectrumDTO.class);*/
        final DataTableResponse response = spectrumMatchService.findAllClusters(searchStr, start,
                length, column, sortDirection);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final String jsonString = mapper.writeValueAsString(response);
        return jsonString;
    }
}
