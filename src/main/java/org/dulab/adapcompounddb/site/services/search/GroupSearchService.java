package org.dulab.adapcompounddb.site.services.search;

import java.util.stream.Collectors;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.exceptions.IllegalSpectrumSearchException;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.MultiFetchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.EmailService;
import org.dulab.adapcompounddb.site.services.io.ExportSearchResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;



import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Service
public class GroupSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupSearchService.class);

    private final IndividualSearchService spectrumSearchService;
    private final ExportSearchResultsService exportSearchResultsService;
    private final SpectrumRepository spectrumRepository;

    private final MultiFetchRepository multiFetchRepository;
    private final EmailService emailService;
    private final SpectrumMatchRepository spectrumMatchRepository;

    @Autowired
    public GroupSearchService(IndividualSearchService spectrumSearchService,
                              @Qualifier("excelExportSearchResultsService") ExportSearchResultsService exportSearchResultsService,
                              SpectrumRepository spectrumRepository,
                              MultiFetchRepository multiFetchRepository,
                              EmailService emailService,
                              SpectrumMatchRepository spectrumMatchRepository) {

        this.spectrumSearchService = spectrumSearchService;
        this.exportSearchResultsService = exportSearchResultsService;
        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.multiFetchRepository = multiFetchRepository;
        this.emailService = emailService;
    }

    public List<SearchResultDTO> groupSearch(UserPrincipal userPrincipal,HttpSession session, SearchParameters userParameters,
        Spectrum querySpectrum, boolean withOntologyLevels, boolean sendResultsToEmail, boolean savedSubmission){

        List<SpectrumMatch> savedMatches = new ArrayList<>();
        Set<Long> deleteMatches = new HashSet<>();
        List<SearchResultDTO> individualSearchResults;

        SearchParameters parameters =
            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
        parameters.merge(userParameters);
        try{
            individualSearchResults = (withOntologyLevels)
                ? spectrumSearchService.searchWithOntologyLevels(userPrincipal, querySpectrum, parameters, savedSubmission, savedMatches, deleteMatches)
                : spectrumSearchService.searchConsensusSpectra(userPrincipal, querySpectrum, parameters, savedSubmission, savedMatches, deleteMatches);

        }catch (IllegalSpectrumSearchException e) {
            LOGGER.error(String.format("Error when searching %s [%d]: %s",
                querySpectrum.getName(), querySpectrum.getId(), e.getMessage()));
            SearchResultDTO searchResultDTO = new SearchResultDTO(querySpectrum);
            searchResultDTO.setErrorMessage(e.getMessage());
            individualSearchResults = Collections.singletonList(searchResultDTO);
        }
        catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search: %s", t.getMessage()), t);
            session.setAttribute("GROUP_SEARCH_ERROR", t.getMessage());
            throw t;
        }
        spectrumMatchRepository.deleteByQuerySpectrumsAndUserId( userPrincipal.getId(),deleteMatches);
        spectrumMatchRepository.saveAll(savedMatches);
        return individualSearchResults;
    }
    @Async
//    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(UserPrincipal userPrincipal, List<File> files, HttpSession session,
                                    SearchParameters userParameters,
                                    boolean withOntologyLevels, boolean sendResultsToEmail, boolean savedSubmission) throws TimeoutException {
        long time1 = System.currentTimeMillis();
//        LOGGER.info("Group search has started");
        List<SpectrumMatch> savedMatches = new ArrayList<>();
        Set<Long> deleteMatches = new HashSet<>();


        try {
            final List<SearchResultDTO> groupSearchDTOList = new ArrayList<>();
            //spectrumMatchRepository.deleteAll();
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

            // Reset entity manager. Otherwise it'll eventually use up the entire memory.
            multiFetchRepository.resetEntityManager();
            spectrumRepository.resetEntityManager();

            long startTime = System.currentTimeMillis();



            boolean showSessionEndedMessage = true;
            int spectrumCount = 0;
            int progressStep = 0;
            float progress = 0F;
            int position = 0;

            //delete old spectrum match by user id before every group search
            //spectrumMatchRepository.deleteByuserPrincipalId(userPrincipal.getId());

            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {

                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                if (spectra == null) continue;
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex) {  // Spectrum querySpectrum : file.getSpectra()
                    if(System.currentTimeMillis() - startTime > 6 * 60 * 60 * 1000)
                    {
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_ERROR_ATTRIBUTE_NAME, "Group search timed out");
                        throw new TimeoutException("Group search timed out");
                    }

                    Spectrum querySpectrum = spectra.get(spectrumIndex);

                    if (Thread.currentThread().isInterrupted()) break;

                    SearchParameters parameters =
                            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
                    parameters.merge(userParameters);
//                    parameters.setLimit(10);

                    List<SearchResultDTO> individualSearchResults;
                    try {
                        individualSearchResults = (withOntologyLevels)
                                ? spectrumSearchService.searchWithOntologyLevels(userPrincipal, querySpectrum, parameters, savedSubmission, savedMatches, deleteMatches)
                                : spectrumSearchService.searchConsensusSpectra(userPrincipal, querySpectrum, parameters, savedSubmission, savedMatches, deleteMatches);
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
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME,
                            groupSearchDTOList);
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_PROGRESS_ATTRIBUTE_NAME,
                            progress);

                    } catch (IllegalStateException e) {
                        if (sendResultsToEmail) {
                            if (showSessionEndedMessage) {
                                LOGGER.warn("It looks like the session has been closed.");
                                showSessionEndedMessage = false;
                            }
                        } else {
                            LOGGER.warn(
                                "It looks like the session has been closed. Stopping the group search.");
                            //return new AsyncResult<>(null);
                        }
                    }

                    if (++spectrumCount % 100 == 0) {
                        long time = System.currentTimeMillis();
                        LOGGER.info(String.format(
                                "Searched %d spectra with the average time %.3f seconds per spectrum",
                                spectrumCount, 1E-3 * (time - startTime) / spectrumCount));
                    }

                    if (spectrumCount % 1000 == 0) {
                        // Reset entity manager. Otherwise it'll eventually use up the entire memory.
                        multiFetchRepository.resetEntityManager();
                        spectrumRepository.resetEntityManager();
                    }
                }
            }
            if(userPrincipal != null) {
                spectrumMatchRepository.deleteByQuerySpectrumsAndUserId(userPrincipal.getId(),
                    deleteMatches);
                spectrumMatchRepository.saveAll(savedMatches);
            }


            long time2 = System.currentTimeMillis();
            double total = (time2 - time1) / 1000.0;
            if (!groupSearchDTOList.isEmpty() && sendResultsToEmail && userPrincipal != null) {
                String tmpdir = System.getProperty("java.io.tmpdir");
                String date = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());
                String filePath = Paths.get(tmpdir, String.format("simple_output_%s.xlsx", date)).toString();
                LOGGER.info(String.format("Writing to file '%s'", filePath));

                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    //export file locally
                    exportSearchResultsService.export(fileOutputStream, groupSearchDTOList);
                    //send email with that file
                    emailService.sendEmailWithAttachment(filePath, userPrincipal.getEmail());
                    //delete the local file
                    Path path = FileSystems.getDefault().getPath(filePath);
                    Files.delete(path);

                } catch (NoSuchFileException e) {
                    LOGGER.error(String.format("%s: no such file or directory: %s", filePath, e.getMessage()), e);
                } catch (IOException e) {
                    LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
                }


//                filePath = Paths.get(userHome, String.format("advanced_output_%s.xlsx", date)).toString();
//                LOGGER.info(String.format("Writing to file '%s'", filePath));
//                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
//                    exportSearchResultsService.exportAll(fileOutputStream, groupSearchDTOList);
//                } catch (IOException e) {
//                    LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
//                }
            }

        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search: %s", t.getMessage()), t);
            session.setAttribute("GROUP_SEARCH_ERROR", t.getMessage());
            throw t;
        }

        if (Thread.currentThread().isInterrupted())
            LOGGER.info("Group search is cancelled");

        return new AsyncResult<>(null);
    }

}
