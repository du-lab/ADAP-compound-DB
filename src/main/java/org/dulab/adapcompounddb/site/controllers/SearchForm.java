package org.dulab.adapcompounddb.site.controllers;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class SearchForm {

    private boolean scoreThresholdCheck = true;

    @Min(value = 0, message = "M/z tolerance must be positive.")
    private float mzTolerance = 0.01F;

    @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
    @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
    private int scoreThreshold;

    private boolean massToleranceCheck = true;

    @Min(value = 0, message = "M/z tolerance must be positive.")
    private float massTolerance = 0.01F;

    private boolean retTimeToleranceCheck = false;

    private float retTimeTolerance = 0.5F;

    public float getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(float mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public boolean isScoreThresholdCheck() {
        return scoreThresholdCheck;
    }

    public void setScoreThresholdCheck(boolean scoreThresholdCheck) {
        this.scoreThresholdCheck = scoreThresholdCheck;
    }

    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(int scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public boolean isMassToleranceCheck() {
        return massToleranceCheck;
    }

    public void setMassToleranceCheck(boolean massToleranceCheck) {
        this.massToleranceCheck = massToleranceCheck;
    }

    public float getMassTolerance() {
        return massTolerance;
    }

    public void setMassTolerance(float massTolerance) {
        this.massTolerance = massTolerance;
    }

    public boolean isRetTimeToleranceCheck() {
        return retTimeToleranceCheck;
    }

    public void setRetTimeToleranceCheck(boolean retTimeToleranceCheck) {
        this.retTimeToleranceCheck = retTimeToleranceCheck;
    }

    public float getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public void setRetTimeTolerance(float retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
    }
}
