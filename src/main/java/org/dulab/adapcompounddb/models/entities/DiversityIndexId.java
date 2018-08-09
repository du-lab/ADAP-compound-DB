package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DiversityIndexId implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "DiversityIndex: The field Cluster is required.")
    @Valid
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    private SpectrumCluster cluster;

    @NotNull(message = "DiversityIndexL The field Category Type is required.")
    @Enumerated(EnumType.STRING)
    private SubmissionCategoryType categoryType;

    public DiversityIndexId() {}

    public DiversityIndexId(SpectrumCluster cluster, SubmissionCategoryType categoryType) {
        this.cluster = cluster;
        this.categoryType = categoryType;
    }

    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    public SubmissionCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(SubmissionCategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DiversityIndexId)) return false;
        DiversityIndexId that = (DiversityIndexId) other;
        return Objects.equals(this.cluster, that.cluster)
                && Objects.equals(this.categoryType, that.categoryType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cluster, categoryType);
    }
}
