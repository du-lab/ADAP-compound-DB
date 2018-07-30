package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.dto.serializers.FileNameExtracter;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class SpectrumDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private long id;

	@NotNull(message = "Spectrum: peak list is required.")
	@Valid
	@JsonIgnore
	private List<Peak> peaks;

	private List<SpectrumProperty> properties;

	private List<SpectrumMatch> matches;

	private List<SpectrumMatch> matches2;

	private SpectrumCluster cluster;

	private boolean consensus;

	private boolean reference;

	private Double precursor;

	private Double retentionTime;
	
	private String chromatographyType;

	@JsonDeserialize(using=FileNameExtracter.class)
	@JsonSetter("file")
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

	public List<Peak> getPeaks() {
		return peaks;
	}

	public void setPeaks(List<Peak> peaks) {
		this.peaks = peaks;
	}

	public List<SpectrumProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<SpectrumProperty> properties) {
		this.properties = properties;
	}

	public List<SpectrumMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<SpectrumMatch> matches) {
		this.matches = matches;
	}

	public List<SpectrumMatch> getMatches2() {
		return matches2;
	}

	public void setMatches2(List<SpectrumMatch> matches2) {
		this.matches2 = matches2;
	}

	public SpectrumCluster getCluster() {
		return cluster;
	}

	public void setCluster(SpectrumCluster cluster) {
		this.cluster = cluster;
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

	public String getChromatographyType() {
		return chromatographyType;
	}

	public void setChromatographyType(String chromatographyType) {
		this.chromatographyType = chromatographyType;
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
