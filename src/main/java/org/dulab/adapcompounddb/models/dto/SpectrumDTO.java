package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

@Deprecated
public class SpectrumDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private long id;

    private boolean consensus;

    private boolean reference;

    private Double precursor;

    private Double retentionTime;

    private String chromatographyTypeLabel;

    private String chromatographyTypeIconPath;

    private String fileName;

    private Integer fileIndex;

    private Integer spectrumIndex;

    private Double significance;

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpectrumDTO)) {
            return false;
        }
        return id == ((SpectrumDTO) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return getName();
    }

    // ****************************
    // ***** Getter/Setter methods *****
    // ****************************

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isConsensus() {
        return consensus;
    }

    public void setConsensus(final boolean consensus) {
        this.consensus = consensus;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(final boolean reference) {
        this.reference = reference;
    }

    public Double getPrecursor() {
        return precursor;
    }

    public void setPrecursor(final Double precursor) {
        this.precursor = precursor;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(final Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getChromatographyTypeLabel() {
        return chromatographyTypeLabel;
    }

    public void setChromatographyTypeLabel(final String chromatographyTypeLabel) {
        this.chromatographyTypeLabel = chromatographyTypeLabel;
    }

    public String getChromatographyTypeIconPath() {
        return chromatographyTypeIconPath;
    }

    public void setChromatographyTypeIconPath(final String chromatographyTypeIconPath) {
        this.chromatographyTypeIconPath = chromatographyTypeIconPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(final Integer fileIndex) {
        this.fileIndex = fileIndex;
    }

    public Integer getSpectrumIndex() {
        return spectrumIndex;
    }

    public void setSpectrumIndex(final Integer spectrumIndex) {
        this.spectrumIndex = spectrumIndex;
    }

    public Double getSignificance() {
        return significance;
    }

    public void setSignificance(final Double significance) {
        this.significance = significance;
    }
}
