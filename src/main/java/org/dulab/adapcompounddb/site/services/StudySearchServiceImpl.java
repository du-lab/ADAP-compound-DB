package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.models.dto.SubmissionMatchDTO;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class StudySearchServiceImpl implements StudySearchService {

    private final SpectrumRepository spectrumRepository;
    private final SubmissionRepository submissionRepository;

    public StudySearchServiceImpl(SpectrumRepository spectrumRepository, SubmissionRepository submissionRepository) {
        this.spectrumRepository = spectrumRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public List<SubmissionMatchDTO> studySearch(UserPrincipal user, Submission submission) {

        List<SpectrumMatch> spectrumMatches = new ArrayList<>();

        Iterable<BigInteger> submissionIds = submissionRepository.findSubmissionIdsByUserAndSubmissionTags(
                user != null ? user.getId() : null, null, null, null);

        int querySubmissionSpectraCount = 0;
        for (File file : submission.getFiles()) {

            for (Spectrum spectrum : file.getSpectra()) {
                SearchParameters searchParameters =
                        SearchParameters.getDefaultParameters(spectrum.getChromatographyType());
                //TODO Change the next line to something like
                // `Map<Long, List<Long>> commonToSpectrumIdsMap = MappingUtils.toMapOfLists(spectrumRepository.preScreenSpectrum(spectrum, searchParameters.getMzTolerance())`
                // Here, the map key will be equal to the number of common peaks, and its value is the list of corresponding spectrum IDs
                //TODO Be careful with conversions from BigInteger to Long
                Map<Long, List<Long>> commonToSpectrumIdsMap = MappingUtils.toMapBigIntegerOfLists(spectrumRepository.preScreenSpectrum(spectrum, searchParameters.getMzTolerance()));

                //TODO Add `List<Long> preScreenedSpectrumIds = getSpectrumIdsWithCommonPeaksAboveThreshold(commonToSpectrumIdsMap, 50)`
                List<Long> preScreenedSpectrumIds = getSpectrumIdsWithCommonPeaksAboveThreshold(commonToSpectrumIdsMap, 50);
                List<SpectrumMatch> matches = MappingUtils.toList(spectrumRepository.matchAgainstClusterableSpectra(
                        submissionIds,
                        spectrum,
                        searchParameters.getScoreThreshold(),
                        searchParameters.getMzTolerance(),
                        searchParameters.getPrecursorTolerance(),
                        searchParameters.getMolecularWeightTolerance()));
                spectrumMatches.addAll(matches);
                querySubmissionSpectraCount++;
            }
        }

        Set<Submission> matchSubmissions = new HashSet<>();

        // For each library submission, this map will contain unique spectra from the
        // submission that matches a spectrum from the user submission
        Map<Submission, Set<Spectrum>> matchSubmissionToMatchSpectraMap = new HashMap<>();

        // For each library submission, this map will contain unique spectra from the
        // user submission that matches a spectrum from the that library submission
        Map<Submission, Set<Spectrum>> matchSubmissionToQuerySpectraMap = new HashMap<>();

        for (SpectrumMatch match : spectrumMatches) {

            if (match.getScore() < 0.7) continue;

            Spectrum querySpectrum = match.getQuerySpectrum();
            Spectrum matchSpectrum = match.getMatchSpectrum();
            Submission matchSubmission = matchSpectrum.getFile().getSubmission();
            matchSubmissions.add(matchSubmission);

            Set<Spectrum> matchSpectrumSet = matchSubmissionToMatchSpectraMap.computeIfAbsent(matchSubmission,
                    k -> new HashSet<>());
            matchSpectrumSet.add(matchSpectrum);

            Set<Spectrum> querySpectrumSet = matchSubmissionToQuerySpectraMap.computeIfAbsent(matchSubmission,
                    k -> new HashSet<>());

            querySpectrumSet.add(querySpectrum);
        }

        List<SubmissionMatchDTO> submissionMatchDTOs = new ArrayList<>();
        for (Submission matchSubmission : matchSubmissions) {
            int cij = matchSubmissionToQuerySpectraMap.get(matchSubmission).size();
            int cji = matchSubmissionToMatchSpectraMap.get(matchSubmission).size();
            int si = querySubmissionSpectraCount;
            int sj;

            int matchSubmissionSpectraCount = 0;
            for (File file : matchSubmission.getFiles()) {
                matchSubmissionSpectraCount += file.getSpectra().size();
            }
            sj = matchSubmissionSpectraCount;

            // edit the bray curtis formula (now when bc close to 0 means low similarity)
            float bc = (float) (cij + cji) / (si + sj);

            SubmissionMatchDTO submissionMatchDTO = new SubmissionMatchDTO(matchSubmission.getId(), matchSubmission.getName(),
                    (int) (bc * 1000), matchSubmission.getExternalId(), matchSubmission.getTags(), matchSubmission.getDescription());
            submissionMatchDTOs.add(submissionMatchDTO);
        }

        submissionMatchDTOs.sort(Comparator.comparingDouble(m -> -1 * m.getScore()));

        return submissionMatchDTOs;
    }

    //TODO Write this method to perform this logic:
    // Get IDs of the library spectra with 8 common peaks.
    // If the number of those spectra exceeds 50, then stop.
    // Otherwise, get IDs of the library spectra with 7 common peaks and combine them with the the IDs for 8 common peaks
    // If the total number of the IDs exceeds 50, the stop.
    // Otherwise, get IDs of the library spectra with 6 common peaks...
    private List<Long> getSpectrumIdsWithCommonPeaksAboveThreshold(Map<Long, List<Long>> commonToSpectrumIdsMap, long commonThreshold) {
        int spectraNumber = 0;
        List<Long> spectraList = new ArrayList<>();
        for (int i=8; i>0; i--) {
            Long keyValue = new Long(i);
            if (commonToSpectrumIdsMap.containsKey(keyValue)) {
                spectraNumber = commonToSpectrumIdsMap.get(keyValue).size() + spectraNumber;
                spectraList.addAll(commonToSpectrumIdsMap.get(keyValue));
                if (spectraNumber > 50) {
                    break;
                }
            }
        }
        return spectraList;
    }
}
