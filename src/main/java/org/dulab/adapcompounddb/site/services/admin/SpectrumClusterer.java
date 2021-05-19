package org.dulab.adapcompounddb.site.services.admin;

import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumClusterer {

    void cluster();

    void removeAll();

    float getProgress();

    void setProgress(float progress);

}
