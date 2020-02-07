package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClusterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Map<Integer, Function<ClusterDTO, Comparable>> COLUMN_TO_FIELD_MAP = new HashMap<>();
    static {
        COLUMN_TO_FIELD_MAP.put(1, ClusterDTO::getQuerySpectrumName);
        COLUMN_TO_FIELD_MAP.put(2, ClusterDTO::getConsensusSpectrumName);
        COLUMN_TO_FIELD_MAP.put(3, ClusterDTO::getSize);
        COLUMN_TO_FIELD_MAP.put(4, ClusterDTO::getScore);
        COLUMN_TO_FIELD_MAP.put(5, ClusterDTO::getAveSignificance);
        COLUMN_TO_FIELD_MAP.put(6, ClusterDTO::getChromatographyTypeLabel);
    }

    // *************************
    // ***** Entity fields *****
    // *************************

    private long clusterId;
    private String querySpectrumName;
    private Long querySpectrumId;
    private String consensusSpectrumName;
    private Integer size;
    private Double score;
    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;
    private String chromatographyTypeLabel;
    private String chromatographyTypePath;

//    private SpectrumDTO consensusSpectrum;
//    private Set<DiversityIndexDTO> diversityIndices;

    public ClusterDTO() {

    }

    public ClusterDTO(SpectrumClusterView view) {
        this.clusterId = view.getId();
        this.consensusSpectrumName = view.getName();
        this.size = view.getSize();
        this.score = view.getScore();
        this.aveSignificance = view.getAverageSignificance();
        this.chromatographyTypeLabel = view.getChromatographyType().getLabel();
        this.chromatographyTypePath = view.getChromatographyType().getIconPath();
    }

    public ClusterDTO spectrumClusterDTO(SpectrumCluster cluster) {
        this.clusterId = cluster.getId();
        this.consensusSpectrumName = cluster.getConsensusSpectrum().getName();
        this.size = cluster.getSize();
        this.score = cluster.getDiameter();
        this.aveSignificance = cluster.getAveSignificance();
        this.minSignificance = cluster.getMinSignificance();
        this.maxSignificance = cluster.getMaxSignificance();
        this.chromatographyTypeLabel = cluster.getConsensusSpectrum().getChromatographyType().getLabel();
        this.chromatographyTypePath = cluster.getConsensusSpectrum().getChromatographyType().getIconPath();
        return this;
    }

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClusterDTO)) {
            return false;
        }
        return clusterId == ((ClusterDTO) other).clusterId;
    }

    public long getClusterId() {
        return clusterId;
    }

    public void setClusterId(final long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(final Double score) {
        this.score = score;
    }

    public Double getAveSignificance() {
        return aveSignificance;
    }

    public void setAveSignificance(final Double aveSignificance) {
        this.aveSignificance = aveSignificance;
    }

    public Double getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(final Double minSignificance) {
        this.minSignificance = minSignificance;
    }

    public Double getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(final Double maxSignificance) {
        this.maxSignificance = maxSignificance;
    }

    public String getQuerySpectrumName() {
        return querySpectrumName;
    }

    public void setQuerySpectrumName(String querySpectrumName) {
        this.querySpectrumName = querySpectrumName;
    }

    public Long getQuerySpectrumId() {
        return querySpectrumId;
    }

    public void setQuerySpectrumId(long querySpectrumId) {
        this.querySpectrumId = querySpectrumId;
    }

    public String getChromatographyTypeLabel() {
        return chromatographyTypeLabel;
    }

    public void setChromatographyTypeLabel(String chromatographyTypeLabel) {
        this.chromatographyTypeLabel = chromatographyTypeLabel;
    }

    public String getChromatographyTypePath() {
        return chromatographyTypePath;
    }

    public void setChromatographyTypePath(String chromatographyTypePath) {
        this.chromatographyTypePath = chromatographyTypePath;
    }

    public String getConsensusSpectrumName() {
        return consensusSpectrumName;
    }

    public void setConsensusSpectrumName(String consensusSpectrumName) {
        this.consensusSpectrumName = consensusSpectrumName;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(clusterId);
    }

    @Override
    public String toString() {
        return "Cluster ID = " + getClusterId();
    }
}
