package org.dulab.site.repositories;

import org.dulab.models.entities.SpectrumCluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpectrumClusterRepository extends JpaRepository<SpectrumCluster, Long> {

    void deleteByIdNotIn(List<Long> ids);
}
