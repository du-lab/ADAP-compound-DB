package org.dulab.adapcompounddb.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
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

    @Autowired
    public SpectrumRestController(final SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = "/findSpectrumBySubmissionId", produces = "application/json")
    public String findSpectrumBySubmissionId(@RequestParam("submissionId") final Long submissionId,
            @RequestParam("start") final Integer start, @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column, @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr, final HttpServletRequest request)
            throws JsonProcessingException {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final DataTableResponse response = spectrumService.findSpectrumBySubmissionId(submissionId, searchStr, start,
                length, column, sortDirection);

        final String jsonString = objectMapper.writeValueAsString(response);
        return jsonString;
    }

    @RequestMapping(value = "/updateReferenceOfAllSpectraOfSubmission", produces = "application/json")
    public Boolean updateReferenceOfAllSpectraOfSubmission(@RequestParam("submissionId") Long submissionId,
            @RequestParam("value") boolean value) throws JsonProcessingException {
        return spectrumService.updateReferenceOfAllSpectraOfSubmission(submissionId, value);
    }
}
