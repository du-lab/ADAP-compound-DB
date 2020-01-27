package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpectrumClusterRepositoryCustom {

    Page<SpectrumCluster> findClusters(
            String searchStr, String species, String source, String disease, Pageable pageable);
}
