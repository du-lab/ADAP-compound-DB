package org.dulab.adapcompounddb.site.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class GroupSearchService {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchService.class);

    private float progress = 0;
    private final IndividualSearchService spectrumSearchService;
    public static final String groupSearchProgress = "group_search_progress";

    @Autowired
    public GroupSearchService(IndividualSearchService spectrumSearchService) {
        this.spectrumSearchService = spectrumSearchService;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Async
//    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(UserPrincipal userPrincipal, List<File> files, HttpSession session,
                                    Set<Long> submissionIds, String species, String source, String disease,
                                    boolean withOntologyLevels) {

        LOGGER.info(String.format("Group search has started (species: %s, source: %s, disease: %s)",
                species != null ? species : "all",
                source != null ? source : "all",
                disease != null ? disease : "all"));

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

            int spectrumCount = 0;
            int progressStep = 0;
            progress = 0F;
            int position = 0;
            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {
                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                if (spectra == null) continue;
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex, ++spectrumCount) {  // Spectrum querySpectrum : file.getSpectra()
                    Spectrum querySpectrum = spectra.get(spectrumIndex);

                    if (Thread.currentThread().isInterrupted()) break;

                    SearchParameters parameters =
                            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
                    parameters.setSpecies(species);
                    parameters.setSource(source);
                    parameters.setDisease(disease);
                    parameters.setSubmissionIds(submissionIds.stream().map(BigInteger::valueOf).collect(Collectors.toSet()));
                    parameters.setLimit(10);

                    List<SearchResultDTO> individualSearchResults = (withOntologyLevels)
                            ? spectrumSearchService.searchWithOntologyLevels(userPrincipal, querySpectrum, parameters)
                            : spectrumSearchService.searchConsensusSpectra(userPrincipal, querySpectrum, parameters);

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
                        session.setAttribute(groupSearchProgress, progress);
                    } catch (IllegalStateException e) {
                        LOGGER.warn("It looks like the session has been closed. Stopping the group search.");
                        return new AsyncResult<>(null);
                    }

                    if (spectrumCount % 100 == 0) {
                        long time = System.currentTimeMillis();
                        LOGGER.info(String.format(
                                "Searched %d spectra with the average time %.3f seconds per spectrum",
                                spectrumCount, 1E-3 * (time - startTime) / spectrumCount));
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search (species: %s, source: %s, disease: %s): %s",
                    species != null ? species : "all",
                    source != null ? source : "all",
                    disease != null ? disease : "all",
                    t.getMessage()), t);
            throw t;
        }

        if (Thread.currentThread().isInterrupted())
            LOGGER.info(String.format("Group search is cancelled (species: %s, source: %s, disease: %s)",
                    species != null ? species : "all",
                    source != null ? source : "all",
                    disease != null ? disease : "all"));

        return new AsyncResult<>(null);
    }
}
