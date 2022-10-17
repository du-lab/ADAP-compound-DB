package org.dulab.adapcompounddb.rest.controllers;

import org.dulab.adapcompounddb.models.entities.Submission;
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

    @RequestMapping(value = "/findStudies",method = RequestMethod.GET)
    public ResponseEntity<?> findPublicStudies(@RequestParam("start") Integer start,
                                               @RequestParam("length") Integer length,
                                               @RequestParam("column")  Integer column,
                                               @RequestParam("sortDirection") String sortDirection) {

        List<Submission> submissionList = submissionService.findSubmissionsPagable(start, length, column, sortDirection);

        return new ResponseEntity<List<Submission>>(submissionList, new HttpHeaders(), HttpStatus.OK);
    }

}

