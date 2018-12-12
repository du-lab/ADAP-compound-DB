package org.dulab.adapcompounddb.site.services;

public interface SpectrumMatchCalculator {

    float getProgress();

    void run();

    void setProgress(float progress);
}
