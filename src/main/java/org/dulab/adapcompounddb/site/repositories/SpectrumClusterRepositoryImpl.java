package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpectrumClusterRepositoryImpl implements SpectrumClusterRepositoryCustom {

    private static final String FIND_CLUSTERS_SQL_QUERY = "select SpectrumCluster.Id as Id, ConsensusSpectrum.Name, " +
            "count(distinct File.SubmissionId) as Size, SpectrumCluster.Diameter as Score, avg(Spectrum.Significance) as AverageSignificance, " +
            "min(Spectrum.Significance) as MinimumSignificance, max(Spectrum.Significance) as MaximumSignificance, " +
            "SpectrumCluster.ChromatographyType from SpectrumCluster " +
            "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
            "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
            "join File on Spectrum.FileId=File.Id " +
            "where ConsensusSpectrum.Name LIKE :search and File.SubmissionId in (:submissionIds) group by SpectrumCluster.Id";

    private static final String FIND_CLUSTERS_SQL_COUNT_QUERY = "select count(distinct SpectrumCluster.Id) from SpectrumCluster " +
            "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
            "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
            "join File on Spectrum.FileId=File.Id " +
            "where ConsensusSpectrum.Name LIKE :search and File.SubmissionId in (:submissionIds)";

    @PersistenceContext
    private EntityManager entityManager;

    public Page<SpectrumClusterView> findClusters(
            String searchStr, Iterable<Long> submissionIds, Pageable pageable) {

        if (submissionIds == null || !submissionIds.iterator().hasNext())
            return new PageImpl<>(new ArrayList<>(0), pageable, 0);


        searchStr = "%" + searchStr + "%";

        BigInteger count = (BigInteger) entityManager.createNativeQuery(FIND_CLUSTERS_SQL_COUNT_QUERY)
                .setParameter("search", searchStr)
                .setParameter("submissionIds", submissionIds)
                .getSingleResult();

        String findClusterSqlQueryWithSort = pageable.getSort().stream()
                .map(order -> String.format(
                        "select * from (%s) as DerivedTable order by (case when %s is null then 1 else 0 end), %s %s",
                        FIND_CLUSTERS_SQL_QUERY, order.getProperty(), order.getProperty(), order.getDirection()))
                .collect(Collectors.joining());


        @SuppressWarnings("unchecked")
        List<SpectrumClusterView> clusters = entityManager
                .createNativeQuery(findClusterSqlQueryWithSort, SpectrumClusterView.class)
                .setParameter("search", searchStr)
                .setParameter("submissionIds", submissionIds)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(clusters, pageable, count.longValue());
    }
}
