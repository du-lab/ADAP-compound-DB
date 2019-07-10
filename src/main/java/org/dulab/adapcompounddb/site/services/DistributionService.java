package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.json.JSONObject;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DistributionService {

    Map<String,Double> calculateClusterPvalue(SpectrumCluster cluster);

    void calculateAllClustersPvalue();

    List<String> getClusterTagDistributions(final SpectrumCluster cluster);

    Map<String,JSONObject> integrateDbAndClusterDistributions(SpectrumCluster cluster);

    void removeAll();

    void calculateAllDistributions();

    void calculateClusterDistributions();

    List<TagDistribution> getAllDistributions();

    //get all the tag distribution of which the Cluster ID is null
    List<TagDistribution> getAllClusterIdNullDistributions();

    TagDistribution getDistribution(long id);

}
