package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SpectrumClusterDTO;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

@Service
public class GroupSearchServiceImpl implements GroupSearchService {

    private float progress = -1F;
    private final SubmissionRepository submissionRepository;
    private final SpectrumRepository spectrumRepository;

    @Autowired
    public GroupSearchServiceImpl(SubmissionRepository submissionRepository, SpectrumRepository spectrumRepository) {
        this.submissionRepository = submissionRepository;
        this.spectrumRepository = spectrumRepository;
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
    @Transactional
    public void groupSearch(long submissionId, HttpSession session, QueryParameters parameters) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
        setSession(submission, parameters, session);
    }

    @Override
    @Transactional
    public void nonSubmittedGroupSearch(Submission submission, HttpSession session, QueryParameters parameters) {
        setSession(submission, parameters, session);
    }

    private void setSession(Submission submission, QueryParameters parameters, HttpSession session) {

        long fullSteps = 0l;
        float progressStep = 0F;
        progress = 0F;
        // Calculate total number of submissions

        for (File f : submission.getFiles()) {
            List<Spectrum> querySpectra = f.getSpectra();
            for (Spectrum s : querySpectra) {
                fullSteps++;
            }
        }

        final List<SpectrumClusterDTO> groupSearchDTOList = new ArrayList<>();

        for (int fileIndex = 0; fileIndex < submission.getFiles().size(); fileIndex++) {

            List<Spectrum> querySpectra = submission.getFiles().get(fileIndex).getSpectra();

            for (int i = 0; i < querySpectra.size(); i++) {

                long querySpectrumId = querySpectra.get(i).getId();
                final List<SpectrumMatch> matches = spectrumRepository.spectrumSearch(
                        SearchType.SIMILARITY_SEARCH, querySpectra.get(i), parameters);

                // get the best match if the match is not null
                if (matches.size() > 0) {
                    groupSearchDTOList.add(saveDTO(matches.get(0), fileIndex, i, querySpectrumId));
                } else {
                    SpectrumMatch noneMatch = new SpectrumMatch();
                    noneMatch.setQuerySpectrum(querySpectra.get(i));
                    groupSearchDTOList.add(saveDTO(noneMatch, fileIndex, i, querySpectrumId));
                }
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                progress = progressStep / fullSteps;
                progressStep = progressStep + 1F;
            }
        }
        progress = -1F;
    }

    private SpectrumClusterDTO saveDTO(SpectrumMatch spectrumMatch, int fileIndex, int spectrumIndex, long querySpectrumId) {
        SpectrumClusterDTO spectrumClusterDTO = new SpectrumClusterDTO();
        if (spectrumMatch.getMatchSpectrum() != null) {
            if (spectrumMatch.getMatchSpectrum().getCluster().getMinPValue() != null) {
                double pValue = spectrumMatch.getMatchSpectrum().getCluster().getMinPValue();
                spectrumClusterDTO.setMinPValue(pValue);
            }
            if (spectrumMatch.getMatchSpectrum().getCluster().getMaxDiversity() != null) {
                double maxDiversity = spectrumMatch.getMatchSpectrum().getCluster().getMaxDiversity();
                spectrumClusterDTO.setMaxDiversity(maxDiversity);
            }
            if (spectrumMatch.getMatchSpectrum().getCluster().getDiseasePValue() != null) {
                double diseasePValue = spectrumMatch.getMatchSpectrum().getCluster().getDiseasePValue();
                spectrumClusterDTO.setDiseasePValue(diseasePValue);
            }
            if (spectrumMatch.getMatchSpectrum().getCluster().getSpeciesPValue() != null) {
                double speciesPValue = spectrumMatch.getMatchSpectrum().getCluster().getSpeciesPValue();
                spectrumClusterDTO.setSpeciesPValue(speciesPValue);
            }
            if (spectrumMatch.getMatchSpectrum().getCluster().getSampleSourcePValue() != null) {
                double sampleSourcePValue = spectrumMatch.getMatchSpectrum().getCluster().getSampleSourcePValue();
                spectrumClusterDTO.setSampleSourcePValue(sampleSourcePValue);
            }

            long matchSpectrumClusterId = spectrumMatch.getMatchSpectrum().getCluster().getId();
            double score = spectrumMatch.getScore();
            int size = spectrumMatch.getMatchSpectrum().getCluster().getSize();
            String matchSpectrumName = spectrumMatch.getMatchSpectrum().getName();
            String chromatographyTypeIconPath = spectrumMatch.getMatchSpectrum().getChromatographyType().getIconPath();
            String chromatographyTypeLabel = spectrumMatch.getMatchSpectrum().getChromatographyType().getLabel();

            spectrumClusterDTO.setMatchSpectrumClusterId(matchSpectrumClusterId);
            spectrumClusterDTO.setConsensusSpectrumName(matchSpectrumName);
            spectrumClusterDTO.setDiameter(score);
            spectrumClusterDTO.setSize(size);
            spectrumClusterDTO.setChromatographyTypeIconPath(chromatographyTypeIconPath);
            spectrumClusterDTO.setChromatographyTypeLabel(chromatographyTypeLabel);

        } else {
            spectrumClusterDTO.setDiameter(null);
        }
        String querySpectrumName = spectrumMatch.getQuerySpectrum().getName();
        spectrumClusterDTO.setFileIndex(fileIndex);
        spectrumClusterDTO.setQuerySpectrumName(querySpectrumName);
        spectrumClusterDTO.setSpectrumIndex(spectrumIndex);
        spectrumClusterDTO.setQuerySpectrumId(querySpectrumId);
        return spectrumClusterDTO;
    }
}
