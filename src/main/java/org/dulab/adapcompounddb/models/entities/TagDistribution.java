package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


@Entity
public class TagDistribution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String label;

    @NotBlank
    private String distribution;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MassSpectrometryType massSpectrometryType;

    private Double pValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ClusterId")
    private SpectrumCluster cluster;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public MassSpectrometryType getMassSpectrometryType() {
        return massSpectrometryType;
    }

    public void setMassSpectrometryType(MassSpectrometryType type) {
        this.massSpectrometryType = type;
    }

    public Double getPValue() {
        return pValue;
    }

    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }


    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(final SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    // Convert Json-String to Map
    @Transient
    public Map<String, DbAndClusterValuePair> getDistributionMap() throws IllegalStateException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(distribution, new TypeReference<Map<String, DbAndClusterValuePair>>(){});
        } catch (IOException e) {
            throw new IllegalStateException("It cannot be converted from Json-String to map!");
        }
    }

    // Convert Map to Json-String
    @Transient
    public void setDistributionMap(Map<String, DbAndClusterValuePair> countMap) throws IllegalStateException {
        try {
            // Default constructor, which will construct the default JsonFactory as necessary, use SerializerProvider as its
            // SerializerProvider, and BeanSerializerFactory as its SerializerFactory.
            this.distribution = new ObjectMapper().writeValueAsString(countMap);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("It cannot be converted to Json-String!");
        }
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
