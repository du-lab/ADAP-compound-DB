package org.dulab.site.services;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.entities.SpectrumCluster;

import java.util.List;

public interface SpectrumMatchService {

    void fillSpectrumMatchTable();

    void cluster(float mzTolerance, int minNumSpectra, float scoreTolerance) throws EmptySearchResultException;

    long getTotalNumberOfClusters();

    List<SpectrumCluster> getAllClusters();

    SpectrumCluster getCluster(long id);
}
