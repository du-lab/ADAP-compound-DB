package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.TagDistribution;

import java.io.IOException;
import java.util.List;

public interface DistributionService {

    Double getClusterPvalue(String tagKey, long id);

    List<TagDistribution> getClusterDistributions(long clusterId);

    List<String> getClusterTagDistributions(final SpectrumCluster cluster);


    void removeAll();

    List<TagDistribution> getAllDistributions();

    //get all the tag distribution of which the Cluster ID is null
    List<TagDistribution> getAllClusterIdNullDistributions();

    TagDistribution getDistribution(long id);

    List<TagDistribution> calculateAllDbDistribution();

    void saveAllDbDistributions();
}
