package org.dulab.adapcompounddb.rest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class GroupSearchController {

    @RequestMapping(value = "/file/group_search_results/", produces = "application/json")
    public String fileGroupSearchResults(
        @RequestParam("start") final Integer start, @RequestParam("length") final Integer length,
        @RequestParam("column") final Integer column, @RequestParam("sortDirection") final String sortDirection,
        @RequestParam("search") final String searchStr) {

        // Assume scoreThreshold = 0.75
        // mzTolerance = 0.01

        /*
        1. Match all spectra from the session to the library and get a list of SpectrumMatch
        2. Sort the List<SpectrumMatch> based on the `column` and `sortDirection` parameters.
        3. Based on "start", "length", return only the required results
        4. Return json-string of DataTableResponse with the matching results
         */
    }
}
