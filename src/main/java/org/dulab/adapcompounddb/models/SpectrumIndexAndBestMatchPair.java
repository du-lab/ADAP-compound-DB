package org.dulab.adapcompounddb.models;

import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

public class SpectrumIndexAndBestMatchPair {


    private int spectrumIndex;
    private SpectrumMatch bestMatch;

    public SpectrumIndexAndBestMatchPair() {
        spectrumIndex = 0;
        bestMatch = new SpectrumMatch();
    }

    public SpectrumIndexAndBestMatchPair(int spectrumIndex, SpectrumMatch bestMatch) {
        this.spectrumIndex = spectrumIndex;
        this.bestMatch = bestMatch;
    }

    public int getSpectrumIndex() {
        return spectrumIndex;
    }

    public void setSpectrumIndex(int spectrumIndex) {
        this.spectrumIndex = spectrumIndex;
    }

    public SpectrumMatch getBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(SpectrumMatch bestMatch) {
        this.bestMatch = bestMatch;
    }
}