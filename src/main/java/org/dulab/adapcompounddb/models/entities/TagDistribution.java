package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
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


    // convert Json to Map
  @Transient
    public Map<String,Integer> getTagDistributionMap() throws JSONException {

        //TODO: fix the errors,
        //TODO: try to follow the highlighted suggestions,
        //TODO: follow the style: 4-space tabs
      ObjectMapper mapper = new ObjectMapper;
      try{
          Map<String, Integer> countMap = mapper.readValue(tagDistribution,Map.class);
          return countMap;
      } catch (JsonParseException e) {
          e.printStackTrace();
      } catch (JsonMappingException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }

  }

  // convert Map to Json
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
