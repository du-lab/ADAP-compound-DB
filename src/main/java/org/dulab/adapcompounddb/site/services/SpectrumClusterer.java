package org.dulab.adapcompounddb.site.services;

import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumClusterer {

    void cluster(/*ChromatographyType type, int minNumSpectra, float scoreTolerance, float mzTolerance*/);

    void removeAll();

    float getProgress();

    void setProgress(float progress);
}
