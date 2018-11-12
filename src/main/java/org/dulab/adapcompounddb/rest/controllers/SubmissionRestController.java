package org.dulab.adapcompounddb.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
@RequestMapping("/submission")
public class SubmissionRestController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionRestController(final SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/findAllSubmissions", produces = "application/json")
    public String findSpectrumBySubmissionId(@RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length, @RequestParam("column") final Integer column,
            @RequestParam("sortDirection") final String sortDirection, @RequestParam("search") final String searchStr,
            final HttpServletRequest request) throws JsonProcessingException {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final DataTableResponse response = submissionService.findAllSubmissionsForResponse(searchStr, start, length, column, sortDirection);

        final String jsonString = objectMapper.writeValueAsString(response);
        return jsonString;
    }
}