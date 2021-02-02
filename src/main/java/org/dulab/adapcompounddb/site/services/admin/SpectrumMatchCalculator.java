package org.dulab.adapcompounddb.site.services.admin;

public interface SpectrumMatchCalculator {

    float getProgress();

    void run();

    void setProgress(float progress);
}
