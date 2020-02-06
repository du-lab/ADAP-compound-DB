package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;

import java.util.List;

public interface SpectrumMatchService {

    //    void fillSpectrumMatchTable(float mzTolerance, float scoreThreshold);

    void cluster(float mzTolerance, int minNumSpectra, float scoreThreshold) throws EmptySearchResultException;

    long getTotalNumberOfClusters();

    List<SpectrumCluster> getAllClusters();

    SpectrumCluster getCluster(long id);

    DataTableResponse findAllClusters(String searchStr, String species, String source, String disease,
                                      Integer start, Integer length, Integer column, String sortDirection);

    DataTableResponse groupSearchSort(final String searchStr, final Integer start, final Integer length,
                                      final Integer column, final String sortDirection,
                                      List<ClusterDTO> spectrumList);

    void loadTagsofCluster(SpectrumCluster cluster);
}
