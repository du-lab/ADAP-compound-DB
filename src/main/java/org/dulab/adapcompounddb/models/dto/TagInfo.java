package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Map;

public class TagInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private Double diversity;
    private Map<String, Integer> countMap;

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public Double getDiversity() {
        return diversity;
    }
    public void setDiversity(final Double diversity) {
        this.diversity = diversity;
    }
    public Map<String, Integer> getCountMap() {
        return countMap;
    }
    public void setCountMap(final Map<String, Integer> countMap) {
        this.countMap = countMap;
    }


}
