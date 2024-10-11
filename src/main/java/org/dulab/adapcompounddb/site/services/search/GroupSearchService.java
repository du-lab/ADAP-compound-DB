package org.dulab.adapcompounddb.site.services.search;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.dulab.adapcompounddb.models.dto.PeakDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;
import org.dulab.adapcompounddb.site.repositories.SearchTaskRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.utils.GroupSearchStorageService;
import org.dulab.adapcompounddb.site.services.utils.GroupSearchStorageService;
import org.json.JSONArray;
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
import java.util.stream.Collectors;

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
    private final SpectrumService spectrumService;

    @Autowired
    public GroupSearchService(IndividualSearchService spectrumSearchService,
                              @Qualifier("csvExportSearchResultsService") ExportSearchResultsService exportSearchResultsService,
                              SpectrumRepository spectrumRepository,
                              MultiFetchRepository multiFetchRepository,
                              EmailService emailService,
                              SpectrumMatchRepository spectrumMatchRepository,
                              SearchTaskRepository searchTaskRepository,
                              GroupSearchStorageService groupSearchStorageService,
                              SpectrumService spectrumService) {

        this.spectrumSearchService = spectrumSearchService;
        this.exportSearchResultsService = exportSearchResultsService;
        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.multiFetchRepository = multiFetchRepository;
        this.emailService = emailService;
        this.searchTaskRepository = searchTaskRepository;
        this.groupSearchStorageService = groupSearchStorageService;
        this.spectrumService = spectrumService;
    }

    @Async
//    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(UserPrincipal userPrincipal, String userIpText, Submission submission,
                                    List<File> files, HttpSession session, SearchParameters userParameters,
                                    Map<BigInteger, String> libraries, boolean withOntologyLevels,
                                    boolean sendResultsToEmail, boolean savedSubmission, String jobId) throws TimeoutException {

        long time1 = System.currentTimeMillis();
        LOGGER.info("Group search has started");

        /**** Group search started ****/
        if (userPrincipal != null && savedSubmission && jobId == null)
            updateSearchTask(userPrincipal, submission, libraries, SearchTaskStatus.RUNNING, session);

        try {
            final List<SearchResultDTO> groupSearchDTOList = Collections.synchronizedList(new ArrayList<>());
            final List<SpectrumDTO> spectrumDTOList = Collections.synchronizedList(new ArrayList<>());
            if(jobId == null) {
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                session.setAttribute("spectrumDTOList", spectrumDTOList);
                session.setAttribute(ControllerUtils.GROUP_SEARCH_PARAMETERS, userParameters);
            }
            // Calculate total number of spectra
            long totalSteps = files.stream()
                    .map(File::getSpectra).filter(Objects::nonNull)
                    .mapToInt(List::size)
                    .sum();

            if (totalSteps == 0) {
                LOGGER.warn("No query spectra for performing a group search");
                if(jobId == null)
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
            List<SpectrumMatch> savedMatches = new ArrayList<>();
            Set<Long> deleteMatches = new HashSet<>();
            Set<String> ontologyLevels = new HashSet<>();
            SearchParameters parameters = null;
            for (int fileIndex = 0; fileIndex < files.size(); ++fileIndex) {

                File file = files.get(fileIndex);
                List<Spectrum> spectra = file.getSpectra();
                int sameNameIndex = 0;
                if (spectra == null) continue;
                for (int spectrumIndex = 0; spectrumIndex < spectra.size(); ++spectrumIndex) {  // Spectrum querySpectrum : file.getSpectra()
                    if (System.currentTimeMillis() - startTime > 96 * 60 * 60 * 1000) {
                        session.setAttribute(ControllerUtils.GROUP_SEARCH_ERROR_ATTRIBUTE_NAME, "Group search timed out");
                        throw new TimeoutException("Group search timed out");
                    }

                    Spectrum querySpectrum = spectra.get(spectrumIndex);

                    if (Thread.currentThread().isInterrupted()) break;

                    parameters =
                            SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
                    parameters.merge(userParameters);
//                    if(jobId != null)
//                        parameters.setSubmissionIds(libraries.keySet());
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

                        // list of unique ontology levels
                        if (searchResult.getOntologyLevel() != null) {
                            ontologyLevels.add(searchResult.getOntologyLevel());
                        }

                    }

                    if (Thread.currentThread().isInterrupted()) break;
                    progress = (float) ++progressStep / totalSteps;
                    //search result dto
                    groupSearchDTOList.addAll(individualSearchResults);

                    if(jobId == null) {
                        //for every sepctra in file we save copy in spectrum DTO
                        SpectrumDTO spectrumDTO = new SpectrumDTO();
                        spectrumDTO.setName(querySpectrum.getName());
                        spectrumDTO.setSpectrumIndex(spectrumIndex);
                        spectrumDTO.setExternalId(querySpectrum.getExternalId());
                        spectrumDTO.setPrecursor(querySpectrum.getPrecursor());
                        spectrumDTO.setRetentionTime(querySpectrum.getRetentionTime());
                        spectrumDTOList.add(spectrumDTO);
                        try {
                            session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME,
                                        groupSearchDTOList);
                            session.setAttribute(ControllerUtils.GROUP_SEARCH_PROGRESS_ATTRIBUTE_NAME,
                                    progress);
                            session.setAttribute(ControllerUtils.SPECTRUM_DTO_LIST, spectrumDTOList);
                            session.setAttribute(ControllerUtils.ONTOLOGY_LEVEL_LIST, ontologyLevels);

                        } catch (IllegalStateException e) {
                            if (sendResultsToEmail) {
                                if (showSessionEndedMessage) {
                                    LOGGER.warn("It looks like the session has been closed.");
                                    showSessionEndedMessage = false;
                                }
                            } else {
//                            LOGGER.warn(
//                                "It looks like the session has been closed. Stopping the group search.");
//                                return new AsyncResult<>(null);
                            }
                        }

                    }
                    else{
//                        groupSearchStorageService.storeResults(jobId, groupSearchDTOList);
                        if (progress < 1) {
                            groupSearchStorageService.updateProgress(jobId, progress);
//                            LOGGER.info(String.format("Group search progress for job ID %s from user %s: %.2f", jobId,
//                                    userPrincipal.getName(),progress*100));
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
       
            if (jobId == null && !groupSearchDTOList.isEmpty()) {

                // Export search results to a session (simple export)
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//                    SearchParameters searchParametersFromSession = (SearchParameters) session.getAttribute(ControllerUtils.GROUP_SEARCH_PARAMETERS);
                    exportSearchResultsService.export(outputStream, groupSearchDTOList, libraries.values(),
                            parameters != null ? parameters.getSearchParametersAsString() : "");  // searchParametersFromSession
                    session.setAttribute(ControllerUtils.GROUP_SEARCH_SIMPLE_EXPORT, outputStream.toByteArray());
                } catch (IOException e) {
                    LOGGER.warn("Error when writing the simple export to the session: " + e.getMessage(), e);
                }

                // Export search results to a session (advanced export)
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//                    SearchParameters searchParametersFromSession = (SearchParameters) session.getAttribute(ControllerUtils.GROUP_SEARCH_PARAMETERS);
                    exportSearchResultsService.exportAll(outputStream, groupSearchDTOList, libraries.values(),
                            parameters != null ? parameters.getSearchParametersAsString() : "");
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
                                parameters != null ? parameters.getSearchParametersAsString() : "");
                        emailService.sendEmailWithAttachment(filePath, userPrincipal.getEmail());
                        //delete the local file
                        Files.delete(FileSystems.getDefault().getPath(filePath));
                    } catch (NoSuchFileException e) {
                        LOGGER.error(String.format("%s: no such file or directory: %s", filePath, e.getMessage()), e);
                    } catch (IOException e) {
                        LOGGER.warn(String.format("Error when writing to file '%s': %s", filePath, e.getMessage()), e);
                    }
                }
            }
            if (userPrincipal != null) {
                //group search api
                if(jobId != null ){
                    if(Thread.currentThread().isInterrupted()){
                        groupSearchStorageService.storeResults(jobId, new ArrayList<>());
                        groupSearchStorageService.addSpectraToResults(jobId, new ArrayList<>());// store empty result
                    }
                    else {
                        Set<Long> spectrumIds = groupSearchDTOList.stream()
                                .map(SearchResultDTO::getSpectrumId)
                                .collect(Collectors.toSet());

                        double scoreThreshold = parameters != null ? parameters.getScoreThreshold() : 0;
                        Set<Long> spectrumIdsWithPeaks = groupSearchDTOList.stream()
                                .filter(r -> r.getScore() > 0.0 && r.getScore() > scoreThreshold)
                                .map(SearchResultDTO::getSpectrumId)
                                .collect(Collectors.toSet());


                        long t = System.currentTimeMillis();
                        List<Object[]> peaksAndSpectrumIds = spectrumRepository.findPeaksAndSpectrumIdsBySpectrumIds(spectrumIdsWithPeaks);
                        long duration1 = System.currentTimeMillis() - t;

                        long t2 = System.currentTimeMillis();
                        List<Object[]> submissionsAndSpectrumIds = spectrumRepository.findSubmissionNamesBySpectrumIds(spectrumIds);
                        long duration2 = System.currentTimeMillis() - t2;

                        Map<Long, List<PeakDTO>> peaksBySpectrumIds = new HashMap<>();
                        for (Object[] result : peaksAndSpectrumIds) {
                            Long spectrumId = (Long) result[1];
                            Peak peak = (Peak) result[0];
                            PeakDTO peakDTO = new PeakDTO(peak.getId(), peak.getMz(), peak.getIntensity());
                            peaksBySpectrumIds.computeIfAbsent(spectrumId, k -> new ArrayList<>());
                            peaksBySpectrumIds.get(spectrumId).add(peakDTO);

                        }
                        Map<Long, String> submissionNamesBySpectrumIds = new HashMap<>();
                        for (Object[] result : submissionsAndSpectrumIds) {
                            Long spectrumId = (Long) result[0];

                            String submissionName = (String) result[1];
                            submissionNamesBySpectrumIds.put(spectrumId, submissionName);
                        }
                        List<Map<String, Object>> spectra = new ArrayList<>();
                        //create matched spectra
                        for (Map.Entry<Long, String> entry : submissionNamesBySpectrumIds.entrySet()) {
                            Map<String, Object> spectraJson = new HashMap<>();
                            Long spectrumId = entry.getKey();
                            spectraJson.put("id", spectrumId);
                            if (peaksBySpectrumIds.containsKey(spectrumId))
                                spectraJson.put("peaks", peaksBySpectrumIds.get(spectrumId));
                            spectraJson.put("submissionName", entry.getValue());

                            spectra.add(spectraJson);
                        }
                        groupSearchStorageService.storeResults(jobId, groupSearchDTOList);
                        groupSearchStorageService.addSpectraToResults(jobId, spectra);

                    }
                    groupSearchStorageService.updateProgress(jobId, 1); //job is done
                    // after job is done and we stored the search results, remove the active job
                    groupSearchStorageService.removeSearchJob(jobId);
                    LOGGER.info("Done group search for user: " + userPrincipal.getName());
                }
                //from browser
                else if(savedSubmission) {
                    //save spectrum match
                    spectrumMatchRepository.deleteByQuerySpectrumsAndUserId(userPrincipal.getId(), deleteMatches);
                    spectrumMatchRepository.saveAll(savedMatches);
                    //update search task status to FINISHED
                    updateSearchTask(userPrincipal, submission, libraries, SearchTaskStatus.FINISHED, session);
                }
            }


        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search: %s", t.getMessage()), t);
            if(jobId != null) {
                groupSearchStorageService.updateProgress(jobId, -1); //job has error
                groupSearchStorageService.clear(jobId);
                LOGGER.info("Cleared resources for job ID: " + jobId);
            }
            else
                session.setAttribute("GROUP_SEARCH_ERROR", t.getMessage());
            throw t;
        }

        /**** Group search interrupted ****/
        if (Thread.currentThread().isInterrupted()) {
            if (userPrincipal != null && savedSubmission && jobId == null)
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
