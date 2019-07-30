package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;


public class GroupSearchDTO implements Serializable {
    private static final long serialVersionUID = -5138706034768474324L;

    // *************************
    // ***** Entity fields *****
    // *************************

    private long id;
    private Double score;
    private String querySpectrumName;
    private String matchSpectrumName;
    private Double maxDiversity;
    private Double minPValue;
    private long matchSpectrumClusterId;
    private int fileIndex;
    private int spectrumIndex;
    private long querySpectrumId;


// *******************************
    // ***** Getters and setters *****
    // *******************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GroupSearchDTO)) {
            return false;
        }
        return id == ((GroupSearchDTO) other).id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getQuerySpectrumName() {
        return querySpectrumName;
    }

    public void setQuerySpectrumName(String querySpectrumName) {
        this.querySpectrumName = querySpectrumName;
    }

    public String getMatchSpectrumName() {
        return matchSpectrumName;
    }

    public void setMatchSpectrumName(String matchSpectrumName) {
        this.matchSpectrumName = matchSpectrumName;
    }

    public Double getMaxDiversity() {
        return maxDiversity;
    }

    public void setMaxDiversity(Double maxDiversity) {
        this.maxDiversity = maxDiversity;
    }

    public Double getMinPValue() {
        return minPValue;
    }

    public void setMinPValue(Double minPValue) {
        this.minPValue = minPValue;
    }

    public long getMatchSpectrumClusterId() {
        return matchSpectrumClusterId;
    }

    public void setMatchSpectrumClusterId(long matchSpectrumClusterId) {
        this.matchSpectrumClusterId = matchSpectrumClusterId;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public int getSpectrumIndex() {
        return spectrumIndex;
    }

    public void setSpectrumIndex(int spectrumIndex) {
        this.spectrumIndex = spectrumIndex;
    }

    public long getQuerySpectrumId() {
        return querySpectrumId;
    }

    public void setQuerySpectrumId(long querySpectrumId) {
        this.querySpectrumId = querySpectrumId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cluster ID = " + getId();
    }
}