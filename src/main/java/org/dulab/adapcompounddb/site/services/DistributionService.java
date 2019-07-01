package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.TagDistribution;

import java.util.List;

public interface DistributionService {

    List<String> getClusterTagDistributions(final SpectrumCluster cluster);
    void removeAll();

    void calculateAllDistributions();

    void calculateClusterDistributions();

    List<TagDistribution> getAllDistributions();

    //get all the tag distribution of which the Cluster ID is null
    List<TagDistribution> getAllClusterIdNullDistributions();

    TagDistribution getDistribution(long id);

}
