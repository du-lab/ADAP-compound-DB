package org.dulab.adapcompounddb.site.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.IllegalSpectrumSearchException;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.io.ExportSearchResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

@Service
public class GroupSearchService {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchService.class);

    private final IndividualSearchService spectrumSearchService;
    private final ExportSearchResultsService exportSearchResultsService;
    private final SpectrumRepository spectrumRepository;

    @Autowired
    public GroupSearchService(IndividualSearchService spectrumSearchService,
                              @Qualifier("excelExportSearchResultsService") ExportSearchResultsService exportSearchResultsService,
                              SpectrumRepository spectrumRepository) {
        this.spectrumSearchService = spectrumSearchService;
        this.exportSearchResultsService = exportSearchResultsService;
        this.spectrumRepository = spectrumRepository;
    }

    @Async
//    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(UserPrincipal userPrincipal, List<File> files, HttpSession session,
                                    SearchParameters userParameters,
                                    boolean withOntologyLevels, boolean sendResultsToEmail) {

//        LOGGER.info("Group search has started");

        try {
            final List<SearchResultDTO> groupSearchDTOList = new ArrayList<>();
            session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);

            // Calculate total number of spectra
            long totalSteps = files.stream()
                    .map(File::getSpectra).filter(Objects::nonNull)
                    .mapToInt(List::size)
                    .sum();

            if (totalSteps == 0) {
                LOGGER.warn("No query spectra for performing a group search");
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                return new AsyncResult<>(null);
            }

            long startTime = System.currentTimeMillis();

            boolean showSessionEndedMessage = true;
            int spectrumCount = 0;
            int progressStep = 0;
            float progress = 0F;
            int position = 0;
            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {
                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                if (spectra == null) continue;
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex) {  // Spectrum querySpectrum : file.getSpectra()
                    Spectrum querySpectrum = spectra.get(spectrumIndex);

                    if (Thread.currentThread().isInterrupted()) break;

                    SearchParameters parameters =
                            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
                    parameters.merge(userParameters);
//                    parameters.setLimit(10);

                    List<SearchResultDTO> individualSearchResults;
                    try {
                        individualSearchResults = (withOntologyLevels)
                                ? spectrumSearchService.searchWithOntologyLevels(userPrincipal, querySpectrum, parameters)
                                : spectrumSearchService.searchConsensusSpectra(userPrincipal, querySpectrum, parameters);
                    } catch (IllegalSpectrumSearchException e) {
                        LOGGER.error(String.format("Error when searching %s [%d]: %s",
                                querySpectrum.getName(), querySpectrum.getId(), e.getMessage()));
                        SearchResultDTO searchResultDTO = new SearchResultDTO(querySpectrum);
                        searchResultDTO.setErrorMessage(e.getMessage());
                        individualSearchResults = Collections.singletonList(searchResultDTO);
                    }

                    if (individualSearchResults.isEmpty())
                        individualSearchResults.add(new SearchResultDTO(querySpectrum));

                    for (SearchResultDTO searchResult : individualSearchResults) {
                        searchResult.setPosition(1 + position++);
                        searchResult.setQueryFileIndex(fileIndex);
                        searchResult.setQuerySpectrumIndex(spectrumIndex);
                    }

                    if (Thread.currentThread().isInterrupted()) break;

                    progress = (float) ++progressStep / totalSteps;
                    groupSearchDTOList.addAll(individualSearchResults);
                    try {
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_PROGRESS_ATTRIBUTE_NAME, progress);
                    } catch (IllegalStateException e) {
                        if (sendResultsToEmail) {
                            if (showSessionEndedMessage) {
                                LOGGER.warn("It looks like the session has been closed.");
                                showSessionEndedMessage = false;
                            }
                        } else {
                            LOGGER.warn("It looks like the session has been closed. Stopping the group search.");
                            return new AsyncResult<>(null);
                        }
                    }

                    if (++spectrumCount % 100 == 0) {
                        long time = System.currentTimeMillis();
                        LOGGER.info(String.format(
                                "Searched %d spectra with the average time %.3f seconds per spectrum",
                                spectrumCount, 1E-3 * (time - startTime) / spectrumCount));
                    }
                }
            }

            if (!groupSearchDTOList.isEmpty() && sendResultsToEmail) {
                String userHome = System.getProperty("user.home");
                String date = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());
                String filePath = Paths.get(userHome, String.format("simple_output_%s.xlsx", date)).toString();
                LOGGER.info(String.format("Writing to file '%s'", filePath));
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    exportSearchResultsService.export(fileOutputStream, groupSearchDTOList);
                } catch (IOException e) {
                    LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
                }

                filePath = Paths.get(userHome, String.format("advanced_output_%s.xlsx", date)).toString();
                LOGGER.info(String.format("Writing to file '%s'", filePath));
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    exportSearchResultsService.exportAll(fileOutputStream, groupSearchDTOList);
                } catch (IOException e) {
                    LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
                }
            }

        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search: %s", t.getMessage()), t);
            throw t;
        }

        if (Thread.currentThread().isInterrupted())
            LOGGER.info("Group search is cancelled");

        return new AsyncResult<>(null);
    }
}
