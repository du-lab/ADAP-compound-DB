package org.dulab.adapcompounddb.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.site.services.FeedbackService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
@RequestMapping("/feedback")
public class FeedbackRestController {

    private final FeedbackService feedbackService;


    public FeedbackRestController(final FeedbackService feedbackService) {
        super();
        this.feedbackService = feedbackService;
    }


    @RequestMapping(value = "/findAllFeedback", produces="application/json")
    public String findAllFeedback(@RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length, @RequestParam("column") final Integer column,
            @RequestParam("sortDirection") final String sortDirection, @RequestParam("search") final String searchStr,
            final HttpServletRequest request) throws JsonProcessingException {
        final DataTableResponse response = feedbackService.findAllFeedbackForResponse(searchStr, start, length, column, sortDirection);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final String jsonString = objectMapper.writeValueAsString(response);
        return jsonString;
    }
}
