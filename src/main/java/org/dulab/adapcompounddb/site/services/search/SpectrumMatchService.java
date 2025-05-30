package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SpectrumMatchService {

    //    void fillSpectrumMatchTable(float mzTolerance, float scoreThreshold);

    void cluster(float mzTolerance, int minNumSpectra, float scoreThreshold) throws EmptySearchResultException;

    long getTotalNumberOfClusters();

    List<SpectrumCluster> getAllClusters();

    SpectrumCluster getCluster(long id);

    DataTableResponse findAllClusters(UserPrincipal user, ChromatographyType chromatographyType,
                                      String searchStr, String species, String source, String disease,
                                      Integer start, Integer length, Integer column, String sortDirection);

//    DataTableResponse groupSearchSort(final String searchStr, final Integer start, final Integer length,
//                                      final Integer column, final String sortDirection,
//                                      List<SearchResultDTO> spectrumList);

    void loadTagsofCluster(SpectrumCluster cluster);

    List<SearchResultDTO> convertSpectrumMatchToClusterDTO(List<SpectrumMatch> matches);

    List<SpectrumMatch> findAllSpectrumMatchByUserIdAndQuerySpectrums(Long userId, List<Long> spectrumIds);
    List<SpectrumMatch> findAllSpectrumMatchesByUserIdAndSubmissionId(Long userId, Long submissionId);
    Page<SpectrumMatch> findAllSpectrumMatchByUserIdAndQuerySpectrumsPageable(Long userId, List<Long> spectrumIds, Integer start,
                                                                              Integer length, String sortColumn, String sortDirection);

    Page<String> findAllDistinctSpectrumByUserIdAndQuerySpectrumsPageable(Long userId,
        List<Long> spectrumIds, Integer start, Integer length, Integer showMatchesOnly,
        String ontologyLevel, Double scoreThreshold, Double massError, Double retTimeError,
        String matchName);

    List<SpectrumMatch> findMatchesByUserIdAndQueryIdAndMatchId(long id, Long spectrumId, Long matchId);

    List<SpectrumMatch> getMatchesByUserAndSpectrumName(long id, String spectrumName, Integer showMatchesOnly, String ontologyLevel, Double scoreThreshold, Double massError,
        Double retTimeError, String matchName);
}
