package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Peak;
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

//    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY "
//            + "AND s.clusterable=TRUE AND s.consensus=FALSE AND s.reference=FALSE AND s.chromatographyType = ?1")
//    Iterable<Spectrum> findUnmatchedByChromatographyType(ChromatographyType chromatographyType);

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY " +
            "AND s.clusterable=true AND s.consensus=false AND s.reference=false AND s.chromatographyType=:type")
    Iterable<Spectrum> findUnmatchedSpectra(@Param("type") ChromatographyType type);

//    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY AND s.consensus=FALSE AND s.reference=FALSE")
//    long countUnmatched();

    long countByMatchesEmptyAndClusterableTrueAndConsensusFalseAndReferenceFalse();

    Iterable<Spectrum> findAllByConsensusFalseAndReferenceFalseAndChromatographyType(
            ChromatographyType chromatographyType);

    @Query("SELECT s FROM Spectrum s WHERE " +
            "(SIZE(s.matches) > 0 OR SIZE(s.matches2) > 0) " +  // 1
            "AND s.consensus=FALSE " +
            "AND s.reference=FALSE " +
            "AND s.chromatographyType=?1")
    Iterable<Spectrum> findSpectraForClustering(ChromatographyType type);

    @Query("SELECT DISTINCT s FROM Spectrum s LEFT JOIN FETCH s.peaks WHERE s.id IN :ids") // Query slow
    Iterable<Spectrum> findSpectraWithPeaksById(@Param("ids") Set<Long> ids);

    @Query("SELECT DISTINCT s FROM Spectrum s LEFT JOIN FETCH s.peaks WHERE s.id = :id") // Query slow
    Iterable<Spectrum> findSpectraWithPeaksBySingleId(@Param("id") Long id);

    @Query("SELECT p FROM Peak p WHERE p.spectrum.id IN :ids")
    Iterable<Peak> findPeaksBySpectrumIds(@Param("ids") Set<Long> spectrumIds);

    long countByConsensusIsFalse();

    //***** Statistics *****

    long countByChromatographyType(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndConsensusTrue(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndClusterableTrue(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndReferenceTrue(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndConsensusFalseAndClusterableFalseAndReferenceFalse(ChromatographyType chromatographyType);

    @Query(value = "select s from Spectrum s " +
            "inner join s.file f " +
            "inner join f.submission sub "
            + "where sub.id = ?1 "
            + "and s.name like %?2%")
    Page<Spectrum> findSpectrumBySubmissionId(Long submissionId, String searchStr, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY "
            + "AND s.consensus=FALSE AND s.chromatographyType = ?1")
    long countUnmatchedBySubmissionChromatographyType(ChromatographyType chromatographyType);

//    @Query(value = "select temp.t from (select reference, "
//            + "(select max(reference) from Spectrum s1 where s1.FileId in (SELECT distinct f.id from File f where f.SubmissionId = :submissionId)) as t "
//            + "from Spectrum s2 where s2.FileId in (SELECT distinct f.id from File f where f.SubmissionId = :submissionId) group by reference) "
//            + "as temp "
//            + "group by temp.t having count(temp.reference) = 1", nativeQuery = true)
//    Integer getSpectrumReferenceOfSubmissionIfSame(@Param("submissionId") Long submissionId);

    @Query("select sum(s.reference) from Spectrum s where s.file.submission.id = :submissionId")
    boolean containsReferenceSpectra(@Param("submissionId") Long submissionId);

    @Query("select s.file.submission.id, sum(s.reference) from Spectrum s where s.file.submission.id in :ids group by s.file.submission.id")
    Iterable<Object[]> getAllSpectrumReferenceBySubmissionIds(@Param("ids") long[] submissionIds);

    @Query("select s.file.submission.id, sum(s.inHouseReference) from Spectrum s " +
            "where s.file.submission.id in :ids group by s.file.submission.id")
    Iterable<Object[]> getAllSpectrumInHouseReferenceBySubmissionIds(@Param("ids") long[] submissionIds);

    @Query("select sum(s.clusterable) from Spectrum s where s.file.submission.id = :submissionId")
    boolean containsClusterableSpectra(@Param("submissionId") Long submissionId);

    @Query("select s.file.submission.id, sum(s.clusterable) from Spectrum s " +
            "where s.file.submission.id in :ids group by s.file.submission.id")
    Iterable<Object[]> getAllSpectrumClusterableBySubmissionIds(@Param("ids") long[] submissionIds);

    @Modifying  //clearAutomatically = true
    @Query(value = "UPDATE Spectrum JOIN File ON File.Id = Spectrum.FileId " +
            "SET Reference = :value WHERE File.SubmissionId = :submissionId", nativeQuery = true)
    void updateReferenceBySubmissionId(@Param("submissionId") long submissionId, @Param("value") boolean value);

    @Modifying  //clearAutomatically = true
    @Query(value = "UPDATE Spectrum JOIN File ON File.Id = Spectrum.FileId " +
            "SET Clusterable = :value WHERE File.SubmissionId = :submissionId", nativeQuery = true)
    void updateClusterableBySubmissionId(@Param("submissionId") long submissionId, @Param("value") boolean value);

    long countByConsensusTrue();

    long countByReferenceTrue();

    @Query("select s.file.submission.id, s.reference, count(s.reference) " + "from Spectrum s "
            + "where s.file.submission.id = :submissionId " + "group by s.file.submission.id, s.reference")
    Boolean getReferenceInfoOfAllSpectra(@Param("submissionId") Long submissionId);

    @Modifying
    @Query("UPDATE Spectrum s SET s.cluster = :cluster WHERE s.id IN (:ids)")
    void updateClusterForSpectra(@Param("cluster") SpectrumCluster cluster, @Param("ids") Set<Long> ids);

    Iterable<Spectrum> findAllByConsensusTrueAndChromatographyTypeAndIntegerMz(
            ChromatographyType chromatographyType, boolean integerMz);

    @Query("select count(s) from Spectrum s where s.file.id in :fileIds")
    long countSpectraByFileIds(@Param("fileIds") long[] fileIds);
}
