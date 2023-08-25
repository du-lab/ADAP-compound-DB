package org.dulab.adapcompounddb.site.controllers;

import java.math.BigInteger;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
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
public class ExportRestController extends BaseController {
    private enum ExportStatus {
        PENDING,
        DONE,
        ERROR
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportRestController.class);

    private final ExportSearchResultsService exportSearchResultsService;
    private final ExcelExportSubmissionService exportSubmissionService;
    private final SpectrumMatchService spectrumMatchService;

    @Autowired
    public ExportRestController(
            @Qualifier("csvExportSearchResultsService") ExportSearchResultsService exportSearchResultsService,
            ExcelExportSubmissionService exportSubmissionService, SpectrumMatchService spectrumMatchService) {

        this.exportSearchResultsService = exportSearchResultsService;
        this.exportSubmissionService = exportSubmissionService;
        this.spectrumMatchService = spectrumMatchService;
    }

    @RequestMapping(value="/export/check_status", method= RequestMethod.GET)
    public ResponseEntity<String> checkStatus(HttpSession session){
        ExportStatus exportStatus = (ExportStatus) session.getAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME);
        return new ResponseEntity<>(exportStatus != null ? exportStatus.toString() : ExportStatus.PENDING.toString(), HttpStatus.OK);
//        if(exportStatus == null || exportStatus == ExportStatus.ERROR)
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        else
//            return new ResponseEntity<>(exportStatus.toString(), HttpStatus.ACCEPTED);
    }
    @RequestMapping(value = "/export/submission/{id:\\d+}/", produces = MediaType.TEXT_PLAIN_VALUE)
    public void exportSubmission(@PathVariable("id") long submissionId, @RequestParam Optional<String> name,
                                 HttpServletResponse response, HttpSession session) {
        try {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s.xlsx\"", name.orElse("export")));
            exportSubmissionService.exportSubmission(session, response.getOutputStream(), submissionId);
        } catch (IOException e) {
            LOGGER.warn("Error when writing to a file: " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/export/session/{attribute}/simple_csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void simpleExport(@PathVariable("attribute") String attributeName,
                             @RequestParam(required = false) Long submissionId,
                             HttpSession session, HttpServletResponse response) {
        export(attributeName, session, response, submissionId, false);
    }

    @RequestMapping(value = "/export/session/{attribute}/advanced_csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public void advancedExport(@PathVariable("attribute") String attributeName,
                               @RequestParam(required = false) Long submissionId,
                               HttpSession session, HttpServletResponse response) {
        export(attributeName, session, response, submissionId, true);
    }

    /**
     * Exports a list stored in the current session into CSV file
     *
     * @param groupSearchResultsAttributeName session attribute name that is used to store the list
     * @param session       current session
     * @param response      HTTP Servlet response
     * @param advanced      if true, all matched are exported. Otherwise, only the best match is exported for each feature
     */
    private void export(String groupSearchResultsAttributeName, HttpSession session, HttpServletResponse response,
                        Long submissionId, boolean advanced) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.zip\"", advanced ? "advanced_export" : "simple_export"));


        List<SearchResultDTO> searchResults;

        Object groupSearchResultsAttribute = session.getAttribute(groupSearchResultsAttributeName);
        if (groupSearchResultsAttribute instanceof List) {
            searchResults = ((List<?>) groupSearchResultsAttribute).stream()
                    .filter(object -> object instanceof SearchResultDTO)
                    .map(object -> (SearchResultDTO) object)
                    .collect(Collectors.toList());
        } else if (submissionId != null && submissionId > 0) {
            List<SpectrumMatch> spectrumMatches = spectrumMatchService.findAllSpectrumMatchesByUserIdAndSubmissionId(
                    this.getCurrentUserPrincipal().getId(), submissionId);
            searchResults = new ArrayList<>(spectrumMatches.size());
            int matchIndex = 0;
            for (SpectrumMatch match : spectrumMatches) {
                SearchResultDTO searchResult = MappingUtils.mapSpectrumMatchToSpectrumClusterView(
                        match, matchIndex++, null, null, null);
                searchResult.setChromatographyTypeLabel(match.getMatchSpectrum() != null ? match.getMatchSpectrum().getChromatographyType().getLabel() : null);
                searchResult.setOntologyLevel(match.getOntologyLevel());
                searchResults.add(searchResult);
            }
        } else {
            LOGGER.warn("Cannot find group search results");
            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.ERROR);
            return;
        }

//        if (!(groupSearchResultsAttribute instanceof List)) {
//            LOGGER.warn(String.format("Attribute %s is not of type List", groupSearchResultsAttribute));
//            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.ERROR);
//            return;
//        }
//
//        Object sessionLibraries = session.getAttribute(ControllerUtils.GROUP_SEARCH_LIBRARIES_USED_FOR_MATCHING);
//        Map<BigInteger, String> librariesUsedForMatching = new HashMap<>();
//        if (sessionLibraries != null) {
//             librariesUsedForMatching = (Map<BigInteger, String>) sessionLibraries;
//                for (Map.Entry<BigInteger, String> library : librariesUsedForMatching.entrySet()) {
//                    String formatedName = library.getValue().replaceAll("<span(.*)</span>", "");
//                    librariesUsedForMatching.put(library.getKey(), formatedName);
//                }
//        }

        Set<String> libraries = searchResults.stream()
                .map(SearchResultDTO::getSubmissionName).filter(Objects::nonNull)
                .collect(Collectors.toSet());

        try {
            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.PENDING);

            if (advanced)
                exportSearchResultsService.exportAll(response.getOutputStream(), searchResults, libraries);
            else
                exportSearchResultsService.export(response.getOutputStream(), searchResults, libraries);

            session.setAttribute(ControllerUtils.EXPORT_PROGRESS_ATTRIBUTE_NAME, ExportStatus.DONE);
        } catch (IOException | ConcurrentModificationException e) {
            LOGGER.warn("Error when writing to a file: " + e.getMessage(), e);
        }
    }
}
