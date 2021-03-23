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
                Map<Long, List<Long>> commonToSpectrumIdsMap = MappingUtils.toMapBigIntegerOfLists(
                        spectrumRepository.preScreenSpectrum(spectrum, searchParameters.getMzTolerance()));

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

    private List<Long> getSpectrumIdsWithCommonPeaksAboveThreshold(Map<Long, List<Long>> commonToSpectrumIdsMap, long commonThreshold) {
        int spectraNumber = 0;
        List<Long> spectraList = new ArrayList<>();
        //TODO you can replace `int i=8; i>0; i--` with `long i=8L; i>0L; i--` and you won't need to convert int to long in line 125
        for (int i=8; i>0; i--) {
            Long keyValue = new Long(i);
            //TODO We want to make this function as fast as possible. In your code, when calling commonToSpectrumIdsMap.containsKey(keyValue) and
            // two-times calling commonToSpectrumIdsMap.get(keyValue), you essentially do the same thing three times!
            // Instead, you can call `List<Long> spectra = commonToSpectrumIdsMap.containsKey(keyValue)`
            // and replace `commonToSpectrumIdsMap.containsKey(keyValue)` with `spectra != null`,
            // and replace `commonToSpectrumIdsMap.get(keyValue)` with `spectra`
            if (commonToSpectrumIdsMap.containsKey(keyValue)) {
                spectraNumber = commonToSpectrumIdsMap.get(keyValue).size() + spectraNumber;
                spectraList.addAll(commonToSpectrumIdsMap.get(keyValue));
                //TODO change 50 to `commonThreshold`. Also you can replace `spectraNumber` with `spectraList.size()` and completely get rid of `spectraNumber`
                if (spectraNumber > 50) {
                    break;
                }
            }
        }
        return spectraList;
    }
}
