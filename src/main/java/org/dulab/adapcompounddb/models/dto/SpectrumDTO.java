package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

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

	// ****************************
	// ***** Standard methods *****
	// ****************************

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isConsensus() {
		return consensus;
	}

	public void setConsensus(boolean consensus) {
		this.consensus = consensus;
	}

	public boolean isReference() {
		return reference;
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}

	public Double getPrecursor() {
		return precursor;
	}

	public void setPrecursor(Double precursor) {
		this.precursor = precursor;
	}

	public Double getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(Double retentionTime) {
		this.retentionTime = retentionTime;
	}

	public String getChromatographyTypeLabel() {
		return chromatographyTypeLabel;
	}

	public void setChromatographyTypeLabel(String chromatographyTypeLabel) {
		this.chromatographyTypeLabel = chromatographyTypeLabel;
	}

	public String getChromatographyTypeIconPath() {
		return chromatographyTypeIconPath;
	}

	public void setChromatographyTypeIconPath(String chromatographyTypeIconPath) {
		this.chromatographyTypeIconPath = chromatographyTypeIconPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}

	public Integer getSpectrumIndex() {
		return spectrumIndex;
	}

	public void setSpectrumIndex(Integer spectrumIndex) {
		this.spectrumIndex = spectrumIndex;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof SpectrumDTO))
			return false;
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
}
