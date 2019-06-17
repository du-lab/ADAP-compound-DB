package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Map;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Entity
public class TagDistribution implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long clusterId;

    @NotBlank
    private String tagKey;

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

    public String getTagKey() { return tagKey; }

    public void setTagKey(String tagName) {
        this.tagKey = tagName;
    }

    public String getTagDistribution() { return tagDistribution; }

    public void setTagDistribution(String tagDistribution) {
        this.tagDistribution = tagDistribution;
    }

    public Double getPValue() { return pValue; }

    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }

   /*@Transient
    public Map<String,Integer> getTagDistributionMap(){


    }*/

   @Transient
    public String setTagDistributionMap(Map<String,Integer> countMap){
        try {
            // Default constructor, which will construct the default JsonFactory as necessary, use SerializerProvider as its
            // SerializerProvider, and BeanSerializerFactory as its SerializerFactory.
            String objectMapper = new ObjectMapper().writeValueAsString(countMap);
            this.tagDistribution = objectMapper;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return tagDistribution;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TagDistribution)) return false;
        return id == ((TagDistribution) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
