package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpectrumClusterRepositoryCustom {

    Page<SpectrumClusterView> findClusters(
            String searchStr, String species, String source, String disease, Pageable pageable);
}
