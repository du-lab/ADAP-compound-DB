package org.dulab.adapcompounddb.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.dulab.adapcompounddb.models.dto.SpectrumTableResponse;
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
	public SpectrumRestController(SpectrumService spectrumService) {
		this.spectrumService = spectrumService;
	}

	@RequestMapping(value="/findSpectrumBySubmissionId", produces="application/json")
	public String findSpectrumBySubmissionId(@RequestParam("submissionId") Long submissionId,
								@RequestParam("start") Integer start,
								@RequestParam("length") Integer length,
								@RequestParam("column") Integer column,
								@RequestParam("sortDirection") String sortDirection,
								@RequestParam("search") String searchStr,
								HttpServletRequest request) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		SpectrumTableResponse response = spectrumService.findSpectrumBySubmissionId(submissionId, searchStr, start, length, column, sortDirection);

		String jsonString = objectMapper.writeValueAsString(response);
		return jsonString;
	}	
}