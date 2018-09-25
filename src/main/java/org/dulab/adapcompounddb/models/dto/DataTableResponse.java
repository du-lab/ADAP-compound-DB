package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTableResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long recordsFiltered;

    private Long recordsTotal;

    private List<? extends Serializable> dataList;

    public DataTableResponse() {
        super();
    }

    public DataTableResponse(final List<? extends Serializable> dataList) {
        this.dataList = dataList;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(final Long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(final Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    @JsonProperty("data")
    public List<? extends Serializable> getSpectrumList() {
        return dataList;
    }

    public void setDataList(final List<? extends Serializable> dataList) {
        this.dataList = dataList;
    }

}
