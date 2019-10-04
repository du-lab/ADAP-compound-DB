package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Validated
public interface SpectrumClusterer {

    void cluster(/*ChromatographyType type, int minNumSpectra, float scoreTolerance, float mzTolerance*/);

    void removeAll();

    float getProgress();

    void setProgress(float progress);

}
