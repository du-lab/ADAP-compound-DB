package org.dulab.models;

public class DatabaseStatistics {

    private Long numSubmittedSpectra;
    private Long numUnmatchedSpectra;
    private Long numConsensusSpectra;

    public Long getNumSubmittedSpectra() {
        return numSubmittedSpectra;
    }

    public void setNumSubmittedSpectra(Long numSubmittedSpectra) {
        this.numSubmittedSpectra = numSubmittedSpectra;
    }

    public Long getNumUnmatchedSpectra() {
        return numUnmatchedSpectra;
    }

    public void setNumUnmatchedSpectra(Long numUnmatchedSpectra) {
        this.numUnmatchedSpectra = numUnmatchedSpectra;
    }

    public Long getNumConsensusSpectra() {
        return numConsensusSpectra;
    }

    public void setNumConsensusSpectra(Long numConsensusSpectra) {
        this.numConsensusSpectra = numConsensusSpectra;
    }
}
