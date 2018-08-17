package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpectrumTableResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long recordsFiltered;

	private Long recordsTotal;

	private List<SpectrumDTO> spectrumList;

	public SpectrumTableResponse() {
		super();
	}

	public SpectrumTableResponse(List<SpectrumDTO> spectrumList) {
		this.spectrumList = spectrumList;
	}

	public Long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public Long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	@JsonProperty("data")
	public List<SpectrumDTO> getSpectrumList() {
		return spectrumList;
	}

	public void setSpectrumList(List<SpectrumDTO> spectrumList) {
		this.spectrumList = spectrumList;
	}

}
