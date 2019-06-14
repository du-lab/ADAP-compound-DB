package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class TagDistribution implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "Id")
    private long clusterId;

    @NotBlank
    private String tagName;

    @NotBlank
    private String tagDistribution;


    private Double pValue;


    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public long getClusterId() { return clusterId; }

    public void setClusterId(long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTagName() { return tagName; }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDistribution() { return tagDistribution; }

    public void setTagDistribution(String tagDistribution) {
        this.tagDistribution = tagDistribution;
    }

    public Double getPValue() { return pValue; }

    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }
}
