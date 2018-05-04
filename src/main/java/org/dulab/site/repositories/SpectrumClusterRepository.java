package org.dulab.site.repositories;

import org.dulab.models.entities.SpectrumCluster;
import org.springframework.data.repository.CrudRepository;

public interface SpectrumClusterRepository extends CrudRepository<SpectrumCluster, Long> {

    void deleteByIdNotIn(long[] ids);
}
