package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.models.dto.SubmissionMatchDTO;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.search.JavaSpectrumSimilarityService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudySearchServiceImpl implements StudySearchService {

    private final JavaSpectrumSimilarityService javaSpectrumSimilarityService;

    @Autowired
    public StudySearchServiceImpl(JavaSpectrumSimilarityService javaSpectrumSimilarityService) {

        this.javaSpectrumSimilarityService = javaSpectrumSimilarityService;
    }

    @Override
    public List<SubmissionMatchDTO> studySearch(UserPrincipal user, Submission submission) {

        List<SpectrumMatch> spectrumMatches = new ArrayList<>();

        int querySubmissionSpectraCount = 0;
        for (File file : submission.getFiles()) {
            long sum_time=0l;
            List<Long> time_list = new ArrayList<>();
            List<Spectrum> spectra = file.getSpectra();
            if (spectra == null) continue;

            for (Spectrum spectrum : spectra) {

                SearchParameters searchParameters =
                        SearchParameters.getDefaultParameters(spectrum.getChromatographyType());
                long time1 = System.currentTimeMillis();
                List<SpectrumMatch> matches =
                        javaSpectrumSimilarityService.searchClusterable(spectrum, searchParameters, user);
                long time2 = System.currentTimeMillis();
                long time_cost = time2 - time1;
                time_list.add(time_cost);
                sum_time = sum_time + time_cost;
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
                List<Spectrum> spectra = file.getSpectra();
                if (spectra != null)
                    matchSubmissionSpectraCount += spectra.size();
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
}
