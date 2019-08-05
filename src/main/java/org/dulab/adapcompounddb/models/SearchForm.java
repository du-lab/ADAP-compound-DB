package org.dulab.adapcompounddb.models;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

public class SearchForm {

    private boolean scoreThresholdCheck = true;

    @Min(value = 0, message = "M/z tolerance must be positive.")
    private double mzTolerance = 0.01;

    @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
    @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
    private int scoreThreshold = 750;

    private boolean massToleranceCheck = true;

    @Min(value = 0, message = "M/z tolerance must be positive.")
    private double massTolerance = 0.01;

    private boolean retTimeToleranceCheck = false;

    private double retTimeTolerance = 0.5;

    private String tags;

    private List<String> availableTags;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    public double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(final double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public boolean isScoreThresholdCheck() {
        return scoreThresholdCheck;
    }

    public void setScoreThresholdCheck(final boolean scoreThresholdCheck) {
        this.scoreThresholdCheck = scoreThresholdCheck;
    }

    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(final int scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public double getFloatScoreThreshold() {
        return scoreThreshold / 1000.0;
    }

    public boolean isMassToleranceCheck() {
        return massToleranceCheck;
    }

    public void setMassToleranceCheck(final boolean massToleranceCheck) {
        this.massToleranceCheck = massToleranceCheck;
    }

    public double getMassTolerance() {
        return massTolerance;
    }

    public void setMassTolerance(final double massTolerance) {
        this.massTolerance = massTolerance;
    }

    public boolean isRetTimeToleranceCheck() {
        return retTimeToleranceCheck;
    }

    public void setRetTimeToleranceCheck(final boolean retTimeToleranceCheck) {
        this.retTimeToleranceCheck = retTimeToleranceCheck;
    }

    public double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public void setRetTimeTolerance(final double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public List<String> getAvailableTags() {
        return availableTags;
    }

    public void setAvailableTags(final List<String> availableTags) {
        this.availableTags = availableTags;
    }
}
