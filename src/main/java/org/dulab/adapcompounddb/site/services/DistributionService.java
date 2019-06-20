package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.TagDistribution;

import java.util.List;

public interface DistributionService {

    void removeAll();

    void calculateAllDistributions();

    List<TagDistribution> getAllDistributions();

    TagDistribution  getDistribution(long id);

}
