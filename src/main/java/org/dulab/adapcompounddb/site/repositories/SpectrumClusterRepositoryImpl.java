package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
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
            "where ConsensusSpectrum.Name LIKE :search and File.SubmissionId in (select distinct * from " +
            "(select SubmissionId from SubmissionTag " +
            "where :species is null or :species='all' or (TagKey='species (common)' and TagValue=:species)) as SpeciesTag " +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :source is null or :source='all' or (TagKey='sample source' and TagValue=:source)) as SourceTag " +
            "using (SubmissionId) " +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :disease is null or :disease='all' or (TagKey='disease' and TagValue=:disease)) as DiseaseTag " +
            "using (SubmissionId)) group by SpectrumCluster.Id";

    private static final String FIND_CLUSTERS_SQL_COUNT_QUERY = "select count(distinct SpectrumCluster.Id) from SpectrumCluster " +
            "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
            "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
            "join File on Spectrum.FileId=File.Id " +
            "where ConsensusSpectrum.Name LIKE :search and File.SubmissionId in (select distinct * from " +
            "(select SubmissionId from SubmissionTag " +
            "where :species is null or :species='all' or (TagKey='species (common)' and TagValue=:species)) as SpeciesTag " +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :source is null or :source='all' or (TagKey='sample source' and TagValue=:source)) as SourceTag " +
            "using (SubmissionId)" +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :disease is null or :disease='all' or (TagKey='disease' and TagValue=:disease)) as DiseaseTag " +
            "using (SubmissionId))";

    @PersistenceContext
    private EntityManager entityManager;

    public Page<SpectrumClusterView> findClusters(
            String searchStr, String species, String source, String disease, Pageable pageable) {

        searchStr = "%" + searchStr + "%";

        BigInteger count = (BigInteger) entityManager.createNativeQuery(FIND_CLUSTERS_SQL_COUNT_QUERY)
                .setParameter("search", searchStr)
                .setParameter("species", species)
                .setParameter("source", source)
                .setParameter("disease", disease)
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
                .setParameter("species", species)
                .setParameter("source", source)
                .setParameter("disease", disease)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(clusters, pageable, count.longValue());
    }
}
