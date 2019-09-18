package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY")
    Iterable<Spectrum> findAllByMatchesIsEmpty();

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY "
            + "AND s.consensus=FALSE AND s.reference=FALSE AND s.chromatographyType = ?1")
    Iterable<Spectrum> findUnmatchedByChromatographyType(ChromatographyType chromatographyType);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY AND s.consensus=FALSE AND s.reference=FALSE")
    long countUnmatched();

    Iterable<Spectrum> findAllByConsensusFalseAndReferenceFalseAndChromatographyType(
            ChromatographyType chromatographyType);

    @Query("SELECT s FROM Spectrum s WHERE " +
            "(SIZE(s.matches) > 0 OR SIZE(s.matches2) > 0) " +  // 1
            "AND s.consensus=FALSE " +
            "AND s.reference=FALSE " +
            "AND s.chromatographyType=?1")
    Iterable<Spectrum> findSpectraForClustering(ChromatographyType type);

    long countByConsensusIsFalse();

    long countByChromatographyTypeAndConsensusFalse(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndConsensusTrue(ChromatographyType chromatographyType);

    @Query(value = "select s from Spectrum s " +
            "inner join s.file f " +
            "inner join f.submission sub "
            + "where sub.id = ?1 "
            + "and s.name like %?2%")
    Page<Spectrum> findSpectrumBySubmissionId(Long submissionId, String searchStr, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY "
            + "AND s.consensus=FALSE AND s.chromatographyType = ?1")
    long countUnmatchedBySubmissionChromatographyType(ChromatographyType chromatographyType);

    @Query(value = "select temp.t from (select reference, "
            + "(select max(reference) from Spectrum s1 where s1.FileId in (SELECT distinct f.id from File f where f.SubmissionId = :submissionId)) as t "
            + "from Spectrum s2 where s2.FileId in (SELECT distinct f.id from File f where f.SubmissionId = :submissionId) group by reference) "
            + "as temp "
            + "group by temp.t having count(temp.reference) = 1", nativeQuery = true)
    Integer getSpectrumReferenceOfSubmissionIfSame(@Param("submissionId") Long submissionId);

    long countByConsensusTrue();

    long countByReferenceTrue();

    @Query("select s.file.submission.id, s.reference, count(s.reference) " + "from Spectrum s "
            + "where s.file.submission.id = :submissionId " + "group by s.file.submission.id, s.reference")
    Boolean getReferenceInfoOfAllSpectra(@Param("submissionId") Long submissionId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Spectrum s SET s.reference = :reference where s.file.id in (SELECT distinct f.id from File f where f.submission.id = :submissionId)")
    int updateReferenceOfAllSpectraOfSubmission(@Param("submissionId") Long submissionId,
                                                @Param("reference") Boolean value);

    @Modifying
    @Query("UPDATE Spectrum s SET s.cluster = :cluster WHERE s.id IN (:ids)")
    void updateClusterForSpectra(@Param("cluster") SpectrumCluster cluster, @Param("ids") Set<Long> ids);
}
