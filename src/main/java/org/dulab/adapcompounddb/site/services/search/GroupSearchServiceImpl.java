package org.dulab.adapcompounddb.site.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Service
public class GroupSearchServiceImpl implements GroupSearchService {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchServiceImpl.class);

    private float progress = 0;
    private final SpectrumRepository spectrumRepository;
    private final SearchServiceSelector searchServiceSelector;

    @Autowired
    public GroupSearchServiceImpl(SpectrumRepository spectrumRepository, SearchServiceSelector searchServiceSelector) {
        this.spectrumRepository = spectrumRepository;
        this.searchServiceSelector = searchServiceSelector;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(UserPrincipal userPrincipal, List<File> files, HttpSession session,
                                    Set<Long> submissionIds, String species, String source, String disease) {

        LOGGER.info(String.format("Group search is started (species: %s, source: %s, disease: %s)",
                species != null ? species : "all",
                source != null ? source : "all",
                disease != null ? disease : "all"));

        try {
            final List<SearchResultDTO> groupSearchDTOList = new ArrayList<>();
            session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);

            // Calculate total number of spectra
            long totalSteps = files.stream().mapToInt(file -> file.getSpectra().size()).sum();

            if (totalSteps == 0) {
                LOGGER.warn("No query spectra for performing a group search");
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                return new AsyncResult<>(null);
            }

            int progressStep = 0;
            progress = 0F;
            int position = 0;
            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {
                long time_sum = 0l;
                List<Long> timeList = new ArrayList<>();
                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex) {  // Spectrum querySpectrum : file.getSpectra()
                    Spectrum querySpectrum = spectra.get(spectrumIndex);

                    if (Thread.currentThread().isInterrupted()) break;

                    IndividualSearchService spectrumSearchService = searchServiceSelector
                            .findByChromatographyType(querySpectrum.getChromatographyType());

//                    SearchParameters parameters = new SearchParameters();
//                    parameters.setScoreThreshold(0.5);
//                    parameters.setMzTolerance(0.01);
                    SearchParameters parameters =
                            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
                    parameters.setSpecies(species);
                    parameters.setSource(source);
                    parameters.setDisease(disease);
                    parameters.setSubmissionIds(submissionIds);

                    long time1 = System.currentTimeMillis();
                    List<SearchResultDTO> individualSearchResults =
                            spectrumSearchService.searchConsensusSpectra(userPrincipal, querySpectrum, parameters);

                    // get the best match if the match is not null
                    SearchResultDTO topSearchResult = individualSearchResults.size() > 0
                            ? individualSearchResults.get(0)
                            : new SearchResultDTO(querySpectrum);

                    topSearchResult.setPosition(1 + position++);
                    topSearchResult.setQueryFileIndex(fileIndex);
                    topSearchResult.setQuerySpectrumIndex(spectrumIndex);
                    long time2 = System.currentTimeMillis();
                    long timeCost = time2 - time1;
                    time_sum = time_sum + timeCost;
                    timeList.add(timeCost);
                    if (Thread.currentThread().isInterrupted()) break;

                    groupSearchDTOList.add(topSearchResult);
                    session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                    progress = (float) ++progressStep / totalSteps;
                }
                System.out.println("Total time cost is : " + time_sum);
                System.out.println("Average time cost for each spectrum is: " + time_sum / file.getSpectra().size());
            }
            try {
                FileWriter writer = new FileWriter("/Users/yliao13/Desktop/prescreen_origin_search_comparison/originals.csv");
                writer.append("Query Spectrum ID");
                writer.append(",");
                writer.append("Match Spectrum ID");
                writer.append(",");
                writer.append("Score");
                writer.append("\n");

                for (SearchResultDTO matchSpectrum : groupSearchDTOList) {
                    writer.append(",");
                    writer.append(String.valueOf(matchSpectrum.getQuerySpectrumIndex()));
                    writer.append(",");
                    writer.append(String.valueOf(matchSpectrum.getMatchSpectumId()));
                    writer.append(",");
                    writer.append(String.valueOf(String.valueOf(matchSpectrum.getScore())));
                    writer.append("\n");
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
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
