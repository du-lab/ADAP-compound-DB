package org.dulab.adapcompounddb.site.services.search;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;
import org.dulab.adapcompounddb.site.repositories.SearchTaskRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.services.utils.GroupSearchStorageService;
import org.dulab.adapcompounddb.site.services.utils.GroupSearchStorageService;
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

import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupSearchService.class);

    private final IndividualSearchService spectrumSearchService;
    private final ExportSearchResultsService exportSearchResultsService;
    private final SpectrumRepository spectrumRepository;

    private final MultiFetchRepository multiFetchRepository;
    private final EmailService emailService;
    private final SpectrumMatchRepository spectrumMatchRepository;

    private final SearchTaskRepository searchTaskRepository;
    private final GroupSearchStorageService groupSearchStorageService;

    @Autowired
    public GroupSearchService(IndividualSearchService spectrumSearchService,
                              @Qualifier("csvExportSearchResultsService") ExportSearchResultsService exportSearchResultsService,
                              SpectrumRepository spectrumRepository,
                              MultiFetchRepository multiFetchRepository,
                              EmailService emailService,
                              SpectrumMatchRepository spectrumMatchRepository,
                              SearchTaskRepository searchTaskRepository,
                              GroupSearchStorageService groupSearchStorageService) {

        this.spectrumSearchService = spectrumSearchService;
        this.exportSearchResultsService = exportSearchResultsService;
        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.multiFetchRepository = multiFetchRepository;
        this.emailService = emailService;
        this.searchTaskRepository = searchTaskRepository;
        this.groupSearchStorageService = groupSearchStorageService;
    }
    @Async
//    @Transactional
    public Future<Void> groupSearch(UserPrincipal userPrincipal, List<File> files,
                                    Set<BigInteger> libraryIds, boolean withOntologyLevels,String jobId) throws TimeoutException {
        try {
            final List<SearchResultDTO> groupSearchDTOList = new ArrayList<>();
            final List<SpectrumDTO> spectrumDTOList = new ArrayList<>();

            // Calculate total number of spectra
            long totalSteps = files.stream()
                    .map(File::getSpectra).filter(Objects::nonNull)
                    .mapToInt(List::size)
                    .sum();

            if (totalSteps == 0) {
                LOGGER.warn("No query spectra for performing a group search");
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

            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {

                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                int sameNameIndex = 0;
                if (spectra == null) continue;
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex) {  // Spectrum querySpectrum : file.getSpectra()
                    if (System.currentTimeMillis() - startTime > 6 * 60 * 60 * 1000) {
                        throw new TimeoutException("Group search timed out");
                    }

                    Spectrum querySpectrum = spectra.get(spectrumIndex);

                    if (Thread.currentThread().isInterrupted()) break;
                    SearchParameters parameters =
                            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());

                    parameters.setSubmissionIds(libraryIds);
//                    parameters.setLimit(10);

                    List<SearchResultDTO> individualSearchResults;
                    try {
                        individualSearchResults = (withOntologyLevels)
                                ? spectrumSearchService.searchWithOntologyLevels(userPrincipal, querySpectrum, parameters, true, null, null)
                                : spectrumSearchService.searchConsensusSpectra(userPrincipal, querySpectrum, parameters, true, null, null);
                    } catch (IllegalSpectrumSearchException e) {
                        LOGGER.error(String.format("Error when searching %s [%d]: %s",
                                querySpectrum.getName(), querySpectrum.getId(), e.getMessage()));
                        SearchResultDTO searchResultDTO = new SearchResultDTO(querySpectrum);
                        searchResultDTO.setErrorMessage(e.getMessage());
                        individualSearchResults = Collections.singletonList(searchResultDTO);
                    }

                    if (individualSearchResults.isEmpty())
                        individualSearchResults.add(new SearchResultDTO(querySpectrum));

                    //search result dto
                    for (SearchResultDTO searchResult : individualSearchResults) {
                        searchResult.setPosition(1 + position++);
                        searchResult.setQueryFileIndex(fileIndex);
                        searchResult.setQuerySpectrumIndex(spectrumIndex);
                    }

                    if (Thread.currentThread().isInterrupted()) break;
                    progress = (float) ++progressStep / totalSteps;
                    //search result dto
                    groupSearchDTOList.addAll(individualSearchResults);
                    groupSearchStorageService.storeResults(jobId, individualSearchResults);
                    groupSearchStorageService.updateProgress(jobId, (int) progress*100);

                    if (++spectrumCount % 100 == 0) {
                        long time = System.currentTimeMillis();
                        LOGGER.info(String.format(
                                "Searched %d spectra with the average time %.3f seconds per spectrum for %s",
                                spectrumCount, 1E-3 * (time - startTime) / spectrumCount));
                    }

                    if (spectrumCount % 1000 == 0) {
                        // Reset entity manager. Otherwise it'll eventually use up the entire memory.
                        multiFetchRepository.resetEntityManager();
                        spectrumRepository.resetEntityManager();
                    }
                }
            }
            //store grousearchDTO list in "session" which can be fetch later in another api
            if (userPrincipal != null) {
                //save spectrum match
//                spectrumMatchRepository.deleteByQuerySpectrumsAndUserId(userPrincipal.getId(), deleteMatches);
//                spectrumMatchRepository.saveAll(savedMatches);
                LOGGER.info("Done saving matches for user: " + userPrincipal.getName());
            }
        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search: %s", t.getMessage()), t);
            throw t;
        }
        return new AsyncResult<>(null);
    }
    @Async
//    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(UserPrincipal userPrincipal, String userIpText, Submission submission,
                                    List<File> files, HttpSession session, SearchParameters userParameters,
                                    Map<BigInteger, String> libraries, boolean withOntologyLevels,
                                    boolean sendResultsToEmail, boolean savedSubmission) throws TimeoutException {

        long time1 = System.currentTimeMillis();
        LOGGER.info("Group search has started");
        List<SpectrumMatch> savedMatches = new ArrayList<>();
        Set<Long> deleteMatches = new HashSet<>();

        /**** Group search started ****/
        if (userPrincipal != null && savedSubmission)
            updateSearchTask(userPrincipal, submission, libraries, SearchTaskStatus.RUNNING, session);

        try {
            final List<SearchResultDTO> groupSearchDTOList = new ArrayList<>();
            final List<SpectrumDTO> spectrumDTOList = new ArrayList<>();
            session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
            session.setAttribute("spectrumDTOList", spectrumDTOList);
            session.setAttribute(ControllerUtils.GROUP_SEARCH_PARAMETERS, userParameters);

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


            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {

                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                int sameNameIndex = 0;
                if (spectra == null) continue;
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex) {  // Spectrum querySpectrum : file.getSpectra()
                    if (System.currentTimeMillis() - startTime > 6 * 60 * 60 * 1000) {
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

                    //search result dto
                    for (SearchResultDTO searchResult : individualSearchResults) {
                        searchResult.setPosition(1 + position++);
                        searchResult.setQueryFileIndex(fileIndex);
                        searchResult.setQuerySpectrumIndex(spectrumIndex);
                    }

                    //for every sepctra in file we save copy in spectrum DTO
                    SpectrumDTO spectrumDTO = new SpectrumDTO();
                    spectrumDTO.setName(querySpectrum.getName());
                    spectrumDTO.setSpectrumIndex(spectrumIndex);
                    spectrumDTO.setExternalId(querySpectrum.getExternalId());
                    spectrumDTO.setPrecursor(querySpectrum.getPrecursor());
                    spectrumDTO.setRetentionTime(querySpectrum.getRetentionTime());

                    spectrumDTOList.add(spectrumDTO);

                    if (Thread.currentThread().isInterrupted()) break;
                    progress = (float) ++progressStep / totalSteps;
                    //search result dto
                    groupSearchDTOList.addAll(individualSearchResults);
                    //spectrum dto
                    try {
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME,
                                groupSearchDTOList);
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_PROGRESS_ATTRIBUTE_NAME,
                                progress);
                        session.setAttribute(ControllerUtils.SPECTRUM_DTO_LIST, spectrumDTOList);

                    } catch (IllegalStateException e) {
                        if (sendResultsToEmail) {
                            if (showSessionEndedMessage) {
                                LOGGER.warn("It looks like the session has been closed.");
                                showSessionEndedMessage = false;
                            }
                        } else {
//                            LOGGER.warn(
//                                "It looks like the session has been closed. Stopping the group search.");
                            //return new AsyncResult<>(null);
                        }
                    }

                    if (++spectrumCount % 100 == 0) {
                        long time = System.currentTimeMillis();
                        LOGGER.info(String.format(
                                "Searched %d spectra with the average time %.3f seconds per spectrum for %s",
                                spectrumCount, 1E-3 * (time - startTime) / spectrumCount, userIpText));
                    }

                    if (spectrumCount % 1000 == 0) {
                        // Reset entity manager. Otherwise it'll eventually use up the entire memory.
                        multiFetchRepository.resetEntityManager();
                        spectrumRepository.resetEntityManager();
                    }
                }
            }

            /**** Group search is done ****/

//            long time2 = System.currentTimeMillis();
//            double total = (time2 - time1) / 1000.0;
            if (!groupSearchDTOList.isEmpty()) {
                SearchParameters searchParametersFromSession = (SearchParameters) session.getAttribute(ControllerUtils.GROUP_SEARCH_PARAMETERS);
                // Export search results to a session (simple export)
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    exportSearchResultsService.export(outputStream, groupSearchDTOList, libraries.values(),
                            searchParametersFromSession.getSearchParametersAsString());
                    session.setAttribute(ControllerUtils.GROUP_SEARCH_SIMPLE_EXPORT, outputStream.toByteArray());
                } catch (IOException e) {
                    LOGGER.warn("Error when writing the simple export to the session: " + e.getMessage(), e);
                }

                // Export search results to a session (advanced export)
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    exportSearchResultsService.exportAll(outputStream, groupSearchDTOList, libraries.values(),
                            searchParametersFromSession.getSearchParametersAsString());
                    session.setAttribute(ControllerUtils.GROUP_SEARCH_ADVANCED_EXPORT, outputStream.toByteArray());
                } catch (IOException e) {
                    LOGGER.warn("Error when writing the advanced export to the session: " + e.getMessage(), e);
                }

                // Send search results to email
                if (sendResultsToEmail && userPrincipal != null) {

                    String tmpdir = System.getProperty("java.io.tmpdir");
                    String date = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());
                    String filePath = Paths.get(tmpdir)
                            .resolve(String.format("simple_output_%s.zip", date))
                            .toString();

                    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                        exportSearchResultsService.export(fileOutputStream, groupSearchDTOList, libraries.values(),
                                searchParametersFromSession.getSearchParametersAsString());
                        emailService.sendEmailWithAttachment(filePath, userPrincipal.getEmail());
                        //delete the local file
                        Files.delete(FileSystems.getDefault().getPath(filePath));
                    } catch (NoSuchFileException e) {
                        LOGGER.error(String.format("%s: no such file or directory: %s", filePath, e.getMessage()), e);
                    } catch (IOException e) {
                        LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
                    }
                }


//                filePath = Paths.get(userHome, String.format("advanced_output_%s.xlsx", date)).toString();
//                LOGGER.info(String.format("Writing to file '%s'", filePath));
//                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
//                    exportSearchResultsService.exportAll(fileOutputStream, groupSearchDTOList);
//                } catch (IOException e) {
//                    LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
//                }
            }

            if (userPrincipal != null && savedSubmission) {
                //save spectrum match
                spectrumMatchRepository.deleteByQuerySpectrumsAndUserId(userPrincipal.getId(), deleteMatches);
                spectrumMatchRepository.saveAll(savedMatches);
                //update search task status to FINISHED
                updateSearchTask(userPrincipal, submission, libraries, SearchTaskStatus.FINISHED, session);
            }

        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search: %s", t.getMessage()), t);
            session.setAttribute("GROUP_SEARCH_ERROR", t.getMessage());
            throw t;
        }

        /**** Group search interrupted ****/
        if (Thread.currentThread().isInterrupted()) {
            if (userPrincipal != null && savedSubmission)
                updateSearchTask(userPrincipal, submission, libraries, SearchTaskStatus.CANCELLED, session);
            LOGGER.info("Group search is cancelled");
        }

        return new AsyncResult<>(null);
    }

//    @Transactional
    public void updateSearchTask(UserPrincipal user, Submission submission, Map<BigInteger, String> filteredLibraries,
                                 SearchTaskStatus status, HttpSession session) {
        Optional<SearchTask> retreivedSearchTask = searchTaskRepository.findByUserIdAndSubmissionId(user.getId(), submission.getId());
        SearchTask searchTask;
        //if there's already a task, update it
        if (retreivedSearchTask.isPresent()) {
            searchTask = retreivedSearchTask.get();
        }
        //save new searchtask
        else {
            searchTask = new SearchTask();
            searchTask.setSubmission(submission);
            searchTask.setUser(user);
        }
        searchTask.setLibraries(filteredLibraries);
        searchTask.setDateTime(new Date());

        byte[] simpleExportData = (byte[]) session.getAttribute(ControllerUtils.GROUP_SEARCH_SIMPLE_EXPORT);
        searchTask.setSimpleExportData(simpleExportData);
        byte[] advancedExportData = (byte[]) session.getAttribute(ControllerUtils.GROUP_SEARCH_ADVANCED_EXPORT);
        searchTask.setAdvancedExportData(advancedExportData);

        searchTask.setStatus(status);
        SearchTask savedSearchTask = searchTaskRepository.save(searchTask);
        if (savedSearchTask == null) {
            LOGGER.warn("Could not update search task with user id: " + user.getId() + "and submission id: "
                    + submission.getId());
        }

    }

}
