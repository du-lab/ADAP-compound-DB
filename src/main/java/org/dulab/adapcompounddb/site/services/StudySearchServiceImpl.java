package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SubmissionMatchDTO;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudySearchServiceImpl implements StudySearchService {

    private final SpectrumRepository spectrumRepository;

    public StudySearchServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    public List<SubmissionMatchDTO> studySearch(Submission submission) {

        final QueryParameters gcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.5)
                .setMzTolerance(0.01);

        List<SpectrumMatch> spectrumMatches = new ArrayList<>();

        //TODO submissionMatchDTOs should contain one entry per matched submission, with `submissionName` is the name
        // of the matched submission (study), and `score` is the Bray-Curtis dissimilarity
        List<SubmissionMatchDTO> submissionMatchDTOS = new ArrayList<>();

        int querySubmissionSpectraCount = 0;
        for(File file : submission.getFiles()){
            for(Spectrum spectrum : file.getSpectra()){
                List<SpectrumMatch> matches = spectrumRepository.spectrumSearch(
                        SearchType.CLUSTERING, spectrum, gcQueryParameters);
                for(SpectrumMatch match: matches){
                    SubmissionMatchDTO submissionMatchDTO = new SubmissionMatchDTO(match.getId(),submission.getName(),
                            (int) (match.getScore()*1000));
                    submissionMatchDTOS.add(submissionMatchDTO);
                }
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

        for (Submission matchSubmission : matchSubmissions) {
            int cij = matchSubmissionToQuerySpectraMap.get(matchSubmission).size();
            int cji = matchSubmissionToMatchSpectraMap.get(matchSubmission).size();
            int si = querySubmissionSpectraCount;
            int sj;

            int matchSubmissionSpectraCount = 0;
            for(File file: matchSubmission.getFiles()){
                //TODO you can replace the inner loop with `matchSubmissionSpectraCount += file.getSpectra().size()`
                for(Spectrum spectrum: file.getSpectra()){
                    matchSubmissionSpectraCount++;
                }
            }
            sj = matchSubmissionSpectraCount;
            float bc = 1 - (float)(cij + cji) / (si + sj);
            System.out.println(bc);
        }

        return submissionMatchDTOS;
    }
}
