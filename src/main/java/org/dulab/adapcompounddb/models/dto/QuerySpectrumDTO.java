package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

public class QuerySpectrumDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private long id;

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

    public QuerySpectrumDTO(String name, long id) {
        this.name = name;
        this.id = id;
    }
}
