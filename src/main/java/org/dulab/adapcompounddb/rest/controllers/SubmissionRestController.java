package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.SubmissionController;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SubmissionRestController {
    @Autowired
    SubmissionService submissionService;

    private static final Logger LOGGER = LogManager.getLogger(SubmissionController.class);

    @RequestMapping(value = "/findStudies",method = RequestMethod.GET, produces ="application/json")
    public String findPublicStudies(@RequestParam("start") Integer start,
                                               @RequestParam("length") Integer length,
                                               @RequestParam("column")  Integer column,
                                               @RequestParam("sortDirection") String sortDirection) throws JsonProcessingException {

        DataTableResponse response;
        try {
            response = submissionService.findSubmissionsPagable(start, length, column, sortDirection);
        }
        catch(Throwable t){
            LOGGER.error(t.getMessage(), t);
            throw t;
        }
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //        return new ResponseEntity<List<Submission>>(submissionList, new HttpHeaders(), HttpStatus.OK);
        return mapper.writeValueAsString(response);
    }

}

