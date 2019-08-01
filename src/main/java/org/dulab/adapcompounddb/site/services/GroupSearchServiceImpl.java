package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.GroupSearchDTO;
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

    private final SubmissionRepository submissionRepository;
    private final SpectrumRepository spectrumRepository;

    @Autowired
    public GroupSearchServiceImpl(SubmissionRepository submissionRepository, SpectrumRepository spectrumRepository) {
        this.submissionRepository = submissionRepository;
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public void groupSearch(long submissionId, HttpSession session, QueryParameters parameters) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
        setSeesion(submission, parameters, session);
    }

    @Override
    @Transactional
    public void nonSubmittedGroupSearch(Submission submission, HttpSession session, QueryParameters parameters) {
        setSeesion(submission, parameters, session);
    }

    private void setSeesion(Submission submission, QueryParameters parameters, HttpSession session) {

        for (int fileIndex = 0; fileIndex < submission.getFiles().size(); fileIndex++) {
            final List<GroupSearchDTO> groupSearchDTOList = new ArrayList<>();

            List<Spectrum> querySpectrum = submission.getFiles().get(fileIndex).getSpectra();

            for (int i = 0; i < querySpectrum.size(); i++) {
                int spectrumIndex = i;
                long querySpectrumId = querySpectrum.get(i).getId();
                final List<SpectrumMatch> matches = spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum.get(i), parameters);

                // get the best match if the match is not null
                if (matches.size() > 0) {
                    groupSearchDTOList.add(saveDTO(matches.get(0), fileIndex, spectrumIndex, querySpectrumId));
                } else {
                    SpectrumMatch noneMatch = new SpectrumMatch();
                    noneMatch.setQuerySpectrum(querySpectrum.get(i));
                    groupSearchDTOList.add(saveDTO(noneMatch, fileIndex, spectrumIndex, querySpectrumId));
                }
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
            }
        }
    }

    private GroupSearchDTO saveDTO(SpectrumMatch spectrumMatch, int fileIndex, int spectrumIndex, long querySpectrumId) {
        GroupSearchDTO groupSearchDTO = new GroupSearchDTO();
        if (spectrumMatch.getMatchSpectrum() != null) {
            if (spectrumMatch.getMatchSpectrum().getCluster().getMinPValue() != null) {
                double pValue = spectrumMatch.getMatchSpectrum().getCluster().getMinPValue();
                groupSearchDTO.setMinPValue(pValue);
            }
            if (spectrumMatch.getMatchSpectrum().getCluster().getMaxDiversity() != null) {
                double maxDiversity = spectrumMatch.getMatchSpectrum().getCluster().getMaxDiversity();
                groupSearchDTO.setMaxDiversity(maxDiversity);
            }
            long matchSpectrumClusterId = spectrumMatch.getMatchSpectrum().getCluster().getId();
            double score = spectrumMatch.getScore();
            String matchSpectrumName = spectrumMatch.getMatchSpectrum().getName();
            groupSearchDTO.setMatchSpectrumClusterId(matchSpectrumClusterId);
            groupSearchDTO.setMatchSpectrumName(matchSpectrumName);
            groupSearchDTO.setScore(score);
        } else {
            groupSearchDTO.setScore(null);
        }
        long id = spectrumMatch.getId();
        String querySpectrumName = spectrumMatch.getQuerySpectrum().getName();
        groupSearchDTO.setFileIndex(fileIndex);
        groupSearchDTO.setId(id);
        groupSearchDTO.setQuerySpectrumName(querySpectrumName);
        groupSearchDTO.setSpectrumIndex(spectrumIndex);
        groupSearchDTO.setQuerySpectrumId(querySpectrumId);
        return groupSearchDTO;
    }
}
