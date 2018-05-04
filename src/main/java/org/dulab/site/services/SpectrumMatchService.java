package org.dulab.site.services;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.entities.SpectrumCluster;

import java.util.List;

public interface SpectrumMatchService {

    void fillSpectrumMatchTable();

    void cluster(float scoreTolerance, int minNumSpectra) throws EmptySearchResultException;

    List<SpectrumCluster> getAllClusters();
}
