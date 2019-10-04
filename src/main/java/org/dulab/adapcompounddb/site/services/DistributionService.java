package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DistributionService {

    Double getClusterPvalue(String tagKey, long id);

    List<TagDistribution> getClusterDistributions(long clusterId);

    List<String> getClusterTagDistributions(final SpectrumCluster cluster);


    void removeAll();

    List<TagDistribution> getAllDistributions();

    //get all the tag distribution of which the Cluster ID is null
    List<TagDistribution> getAllClusterIdNullDistributions();

    TagDistribution getDistribution(long id);

    List<TagDistribution> calculateAllDbDistributions(List<SubmissionTag> tags);

    List<TagDistribution> calculateClusterDistributions(List<SubmissionTag> tags,
                                                        MassSpectrometryType massSpectrometryType,
                                                        Map<String, Map<String, Integer>> dbCountMaps);

    Map<String, Map<String, Integer>> getAllDbCountMaps(MassSpectrometryType massSpectrometryType);

    void saveAllDbDistributions();
}
