package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
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

    //TODO Return List<SubmissionMatchDTO> and display it in study_search.jsp
    @Override
    public String studySearch(Submission submission) {
        final QueryParameters gcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.5)
                .setMzTolerance(0.01);
        List<SpectrumMatch> spectrumMatches = new ArrayList<>();

        //TODO You don't need to store spectra in a set. You just need to count their number,
        // so just use `int` instead of `Set<Spectrum>`
        Set<Spectrum> querySubmissionSpectraSet = new HashSet<>();
        for(File file : submission.getFiles()){
            for(Spectrum spectrum : file.getSpectra()){
                List<SpectrumMatch> matches = spectrumRepository.spectrumSearch(
                        SearchType.CLUSTERING, spectrum, gcQueryParameters);
                spectrumMatches.addAll(matches);
                querySubmissionSpectraSet.add(spectrum);
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
            int si = querySubmissionSpectraSet.size();
            int sj;

            //TODO Again, you can just use `int` variable to count the number of spectra.
            // Notice that we use sets in `matchSubmissionToMatchSpectraMap` and `matchSubmissionToQuerySpectraMap`
            // because we want to find the number of unique spectra. But each submission already contains only unique
            // spectra, so we don't need sets here.
            Set<Spectrum> matchSubmissionSpectraSet = new HashSet<>();
            for(File file: matchSubmission.getFiles()){
                for(Spectrum spectrum: file.getSpectra()){
                    matchSubmissionSpectraSet.add(spectrum);
                }
            }
            sj = matchSubmissionSpectraSet.size();
            //TODO Here you divide integer number by an integer number, so `(cij + cji) / (si + sj)` will always be zero.
            float bc = 1 - (cij + cji) / (si + sj);
            System.out.println(bc);
        }

        return null;
    }
}
