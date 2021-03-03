package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
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

//    private static final String FIND_CLUSTERS_SQL_QUERY =
//
//    private static final String FIND_CLUSTERS_SQL_COUNT_QUERY = ;

    /**
     * Return the number of consensus spectra associated with given submissionIds and search string
     *
     * @param submissionIds collection of submission IDs
     * @param search        string for filtering spectra by their names
     * @return number of consensus spectra
     */
    private BigInteger countAllConsensusSpectra(
            Iterable<BigInteger> submissionIds, ChromatographyType chromatographyType, String search) {

        String sqlQuery = "select count(distinct SpectrumCluster.Id) from SpectrumCluster " +
                "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
                "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
                "join File on Spectrum.FileId=File.Id " +
                "where ConsensusSpectrum.Name LIKE :search " +
                "and (:type is null or ConsensusSpectrum.ChromatographyType = :type) " +
                "and File.SubmissionId in (:submissionIds)";

        return (BigInteger) entityManager.createNativeQuery(sqlQuery)
                .setParameter("type", chromatographyType != null ? chromatographyType.name() : null)
                .setParameter("search", "%" + search + "%")
                .setParameter("submissionIds", submissionIds)
                .getSingleResult();
    }

    /**
     * Return the number of reference spectra associated with given submissionIds and search string
     *
     * @param submissionIds collection of submission IDs
     * @param search        string for filtering spectra by their names
     * @return number of reference spectra
     */
    private BigInteger countAllReferenceSpectra(
            Iterable<BigInteger> submissionIds, ChromatographyType chromatographyType, String search) {

        String sqlQuery = "select count(*) from Spectrum join File on Spectrum.FileId=File.Id " +
                "where Spectrum.Reference is true and Spectrum.Name like :search " +
                "and (:type is null or Spectrum.ChromatographyType = :type) and File.SubmissionId in (:submissionIds)";

        return (BigInteger) entityManager.createNativeQuery(sqlQuery)
                .setParameter("type", chromatographyType != null ? chromatographyType.name() : null)
                .setParameter("search", "%" + search + "%")
                .setParameter("submissionIds", submissionIds)
                .getSingleResult();
    }

    @PersistenceContext
    private EntityManager entityManager;

    public Page<SpectrumClusterView> findClusters(
            ChromatographyType chromatographyType, String search, Iterable<BigInteger> submissionIds, Pageable pageable) {

        if (submissionIds == null || !submissionIds.iterator().hasNext())
            return new PageImpl<>(new ArrayList<>(0), pageable, 0);

        String sqlQuery = "select UUID_SHORT() as uniqueId, ConsensusSpectrum.Id as Id, SpectrumCluster.Id as ClusterId, ConsensusSpectrum.Name, " +
                "count(distinct File.SubmissionId) as Size, SpectrumCluster.Diameter as Score, NULL AS MassError, NULL AS RetTimeError, " +
                "avg(Spectrum.Significance) as AverageSignificance, " +
                "min(Spectrum.Significance) as MinimumSignificance, max(Spectrum.Significance) as MaximumSignificance, " +
                "SpectrumCluster.ChromatographyType from SpectrumCluster " +
                "join Spectrum on SpectrumCluster.Id=Spectrum.ClusterId " +
                "join Spectrum as ConsensusSpectrum on SpectrumCluster.ConsensusSpectrumId=ConsensusSpectrum.Id " +
                "join File on Spectrum.FileId=File.Id " +
                "where ConsensusSpectrum.Name LIKE :search " +
                "and (:type is null or ConsensusSpectrum.ChromatographyType = :type) " +
                "and File.SubmissionId in (:submissionIds) group by SpectrumCluster.Id " +
                "union all " +
                "select UUID_SHORT() as uniqueId, Spectrum.Id, null, Spectrum.Name, 1, null, null, null, null, null, null, Spectrum.ChromatographyType from Spectrum " +
                "join File on Spectrum.FileId=File.Id " +
                "where Spectrum.Reference is true and Spectrum.Name like :search " +
                "and (:type is null or Spectrum.ChromatographyType = :type) and File.SubmissionId in (:submissionIds)";

        String findClusterSqlQueryWithSort = pageable.getSort().stream()
                .map(order -> String.format(
                        "select * from (%s) as DerivedTable order by (case when %s is null then 1 else 0 end), %s %s",
                        sqlQuery, order.getProperty(), order.getProperty(), order.getDirection()))
                .collect(Collectors.joining());

        @SuppressWarnings("unchecked")
        List<SpectrumClusterView> clusters = entityManager
                .createNativeQuery(findClusterSqlQueryWithSort, SpectrumClusterView.class)
                .setParameter("type", chromatographyType != null ? chromatographyType.name() : null)
                .setParameter("search", "%" + search + "%")
                .setParameter("submissionIds", submissionIds)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        BigInteger consensusSpectraCount = countAllConsensusSpectra(submissionIds, chromatographyType, search);
        BigInteger referenceSpectraCount = countAllReferenceSpectra(submissionIds, chromatographyType, search);

        return new PageImpl<>(clusters, pageable, consensusSpectraCount.longValue() + referenceSpectraCount.longValue());
    }
}
