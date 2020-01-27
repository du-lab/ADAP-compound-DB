package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class SpectrumClusterRepositoryImpl implements SpectrumClusterRepositoryCustom {

    private static final String FIND_CLUSTERS_SQL_QUERY = "select SpectrumCluster.* from SpectrumCluster " +
            "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
            "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
            "join File on Spectrum.FileId=File.Id " +
            "where :search is not null and File.SubmissionId in (select distinct * from " +
            "(select SubmissionId from SubmissionTag " +
            "where :species is null or (TagKey='species (common)' and TagValue=:species)) as SpeciesTag " +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :source is null or (TagKey='sample source' and TagValue=:source)) as SourceTag " +
            "using (SubmissionId)" +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :disease is null or (TagKey='disease' and TagValue=:disease)) as DiseaseTag " +
            "using (SubmissionId))";

    private static final String FIND_CLUSTERS_SQL_COUNT_QUERY = "select count(*) from SpectrumCluster " +
            "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
            "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
            "join File on Spectrum.FileId=File.Id " +
            "where :search is not null and File.SubmissionId in (select distinct * from " +
            "(select SubmissionId from SubmissionTag " +
            "where :species is null or (TagKey='species (common)' and TagValue=:species)) as SpeciesTag " +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :source is null or (TagKey='sample source' and TagValue=:source)) as SourceTag " +
            "using (SubmissionId)" +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :disease is null or (TagKey='disease' and TagValue=:disease)) as DiseaseTag " +
            "using (SubmissionId))";

    @PersistenceContext
    private EntityManager entityManager;

    public Page<SpectrumCluster> findClusters(
            String searchStr, String species, String source, String disease, Pageable pageable) {

        BigInteger count = (BigInteger) entityManager.createNativeQuery(FIND_CLUSTERS_SQL_COUNT_QUERY)
                .setParameter("search", searchStr)
                .setParameter("species", species)
                .setParameter("source", source)
                .setParameter("disease", disease)
                .getSingleResult();

        String sort = pageable.getSort().stream()
                .map(order -> String.format(" order by %s %s", order.getProperty(), order.getDirection()))
                .collect(Collectors.joining());

        @SuppressWarnings("unchecked")
        List<SpectrumCluster> clusters = entityManager
                .createNativeQuery(FIND_CLUSTERS_SQL_QUERY + sort, SpectrumCluster.class)
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
