package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.Collection;

import org.springframework.stereotype.Repository;

@Repository
public interface SpectrumClusterRepositoryCustom {

    Page<SpectrumClusterView> findClusters(ChromatographyType chromatographyType,
                                           String searchStr, Iterable<BigInteger> submissionIds, Pageable pageable);
}
