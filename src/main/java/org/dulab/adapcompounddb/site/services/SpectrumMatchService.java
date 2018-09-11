package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;

public interface SpectrumMatchService {

    //    void fillSpectrumMatchTable(float mzTolerance, float scoreThreshold);

    void cluster(float mzTolerance, int minNumSpectra, float scoreThreshold) throws EmptySearchResultException;

    long getTotalNumberOfClusters();

    List<SpectrumCluster> getAllClusters();

    SpectrumCluster getCluster(long id);

    DataTableResponse findAllClusters(String searchStr, Integer start, Integer length, Integer column,
            String sortDirection);
}
