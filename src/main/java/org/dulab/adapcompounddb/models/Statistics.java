package org.dulab.adapcompounddb.models;

public class Statistics {

    private Long numSpectra;
    private Long numClusterableSpectra;
    private Long numConsensusSpectra;
    private Long numReferenceSpectra;
    private Long numOtherSpectra;
    private Long numSpectrumMatches;

    public Long getNumSpectra() {
        return numSpectra;
    }

    public void setNumSpectra(Long numSpectra) {
        this.numSpectra = numSpectra;
    }

    public Long getNumClusterableSpectra() {
        return numClusterableSpectra;
    }

    public void setNumClusterableSpectra(Long numClusterableSpectra) {
        this.numClusterableSpectra = numClusterableSpectra;
    }

    public Long getNumConsensusSpectra() {
        return numConsensusSpectra;
    }

    public void setNumConsensusSpectra(Long numConsensusSpectra) {
        this.numConsensusSpectra = numConsensusSpectra;
    }

    public Long getNumReferenceSpectra() {
        return numReferenceSpectra;
    }

    public void setNumReferenceSpectra(Long numReferenceSpectra) {
        this.numReferenceSpectra = numReferenceSpectra;
    }

    public Long getNumOtherSpectra() {
        return numOtherSpectra;
    }

    public void setNumOtherSpectra(Long numOtherSpectra) {
        this.numOtherSpectra = numOtherSpectra;
    }

    public Long getNumSpectrumMatches() {
        return numSpectrumMatches;
    }

    public void setNumSpectrumMatches(Long numSpectrumMatches) {
        this.numSpectrumMatches = numSpectrumMatches;
    }
}
