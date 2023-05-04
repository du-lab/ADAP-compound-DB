package org.dulab.adapcompounddb.site.controllers;

import java.math.BigInteger;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.io.ExcelExportSubmissionService;
import org.dulab.adapcompounddb.site.services.io.ExportSearchResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ExportRestController {
    private enum ExportStatus {
        PENDING,
        DONE,
        ERROR
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportRestController.class);

    private final ExportSearchResultsService exportSearchResultsService;
    private final ExcelExportSubmissionService exportSubmissionService;

    @Autowired
    public ExportRestController(
            @Qualifier("excelExportSearchResultsService") ExportSearchResultsService exportSearchResultsService,
            ExcelExportSubmissionService exportSubmissionService) {

        this.exportSearchResultsService = exportSearchResultsService;
        this.exportSubmissionService = exportSubmissionService;
    }

    @RequestMapping(value="/export/check_status", method= RequestMethod.GET)
    public ResponseEntity<String> checkStatus(HttpSession session){
        ExportStatus exportStatus = (ExportStatus) session.getAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME);
        if(exportStatus == null || exportStatus == ExportStatus.ERROR)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(exportStatus.toString(), HttpStatus.ACCEPTED);
    }
    @RequestMapping(value = "/export/submission/{id:\\d+}/", produces = MediaType.TEXT_PLAIN_VALUE)
    public void exportSubmission(@PathVariable("id") long submissionId, @RequestParam Optional<String> name,
                                 HttpServletResponse response) {
        try {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s.xlsx\"", name.orElse("export")));
            exportSubmissionService.exportSubmission(response.getOutputStream(), submissionId);

        } catch (IOException e) {
            LOGGER.warn("Error when writing to a file: " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/export/session/{attribute}/simple_csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void simpleExport(@PathVariable("attribute") String attributeName,
                             HttpSession session, HttpServletResponse response) {
        export(attributeName, session, response, false);
    }

    @RequestMapping(value = "/export/session/{attribute}/advanced_csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void advancedExport(@PathVariable("attribute") String attributeName,
                               HttpSession session, HttpServletResponse response) {
        export(attributeName, session, response, true);
    }

    /**
     * Exports a list stored in the current session into CSV file
     *
     * @param attributeName session attribute name that is used to store the list
     * @param session       current session
     * @param response      HTTP Servlet response
     * @param advanced      if true, all matched are exported. Otherwise, only the best match is exported for each feature
     */
    private void export(String attributeName, HttpSession session, HttpServletResponse response, boolean advanced) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.xlsx\"", advanced ? "advanced_export" : "simple_export"));

        Object attribute = session.getAttribute(attributeName);
        if (!(attribute instanceof List)) {
            LOGGER.warn(String.format("Attribute %s is not of type List", attributeName));
            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.ERROR);
            return;
        }

        Object sessionLibraries = session.getAttribute(ControllerUtils.GROUP_SEARCH_LIBRARIES_USED_FOR_MATCHING);
        Map<BigInteger, String> librariesUsedForMatching = new HashMap<>();
        if (sessionLibraries != null) {
             librariesUsedForMatching = (Map<BigInteger, String>) sessionLibraries;
                for (Map.Entry<BigInteger, String> library : librariesUsedForMatching.entrySet()) {
                    String formatedName = library.getValue().replaceAll("<span(.*?)</span>", "");
                    librariesUsedForMatching.put(library.getKey(), formatedName);
                }
        }

        try {
            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.PENDING);
            List<SearchResultDTO> searchResults = ((List<?>) attribute).stream()
                    .filter(object -> object instanceof SearchResultDTO)
                    .map(object -> (SearchResultDTO) object)
                    .collect(Collectors.toList());

            if (advanced)
                exportSearchResultsService.exportAll(response.getOutputStream(), searchResults, librariesUsedForMatching.values());
            else
                exportSearchResultsService.export(response.getOutputStream(), searchResults, librariesUsedForMatching.values());

            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.DONE);
        } catch (IOException | ConcurrentModificationException e) {
            LOGGER.warn("Error when writing to a file: " + e.getMessage(), e);
        }
    }
}
