package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface

SpectrumClusterRepository extends JpaRepository<SpectrumCluster, Long> {

    void deleteByIdNotIn(List<Long> ids);

    @Modifying
    @Query("select c FROM SpectrumCluster c")
    Iterable<SpectrumCluster> getAllClusters();

    @Modifying
    @Query("DELETE FROM SpectrumCluster c WHERE 1 = 1")
    void deleteAllEmptyClusters();

    @Modifying
    @Query("DELETE FROM Spectrum c WHERE consensus = 1")
    void deleteAllConsensusSpectra();

    @Query(value = "select s from SpectrumCluster s "
            + "where "
            + "s.consensusSpectrum.name like %:search% "
            + "OR s.consensusSpectrum.chromatographyType like %:search%")
    Page<SpectrumCluster> findClusters(@Param("search") String searchStr, Pageable pageable);

    //    @Query("select cluster from SpectrumCluster cluster " +
//            "join cluster.spectra spectrum join spectrum.file.submission.tags tag " +
//            "where (:species is null or (tag.tagKey='species (common)' and tag.tagValue=:species)) " +
//            "and (:source is null or (tag.tagKey='sample source' and tag.tagValue=:source)) " +
//            "and (:disease is null or (tag.tagKey='disease' and tag.tagValue=:disease))" +
//            "and cluster.consensusSpectrum.name like %:search%")
//    @Query("select cluster from SpectrumCluster cluster " +
//            "join cluster.spectra spectrum " +
//            "where cluster.consensusSpectrum.name like %:search% " +
//            "and spectrum.file.submission.id in (" +
//            "select tag.submission.id from SubmissionTag tag " +
//            "join SubmissionTag tag2 join SubmissionTag tag3 " +
//            "where (:species is null or (tag.tagKey='species (common)' and tag.tagValue=:species)) " +
//            "and (:source is null or (tag2.tagKey='sample source' and tag2.tagValue=:source)) " +
//            "and (:disease is null or (tag3.tagKey='disease' and tag3.tagValue=:disease)) " +
//            "group by tag.submission.id having count(tag) >= 3)")
    @Query("select distinct cluster from SpectrumCluster cluster " +
            "join cluster.spectra spectrum join spectrum.file.submission.tags speciesTags " +
            "join spectrum.file.submission.tags sourceTags join spectrum.file.submission.tags diseaseTags " +
            "where cluster.consensusSpectrum.name like %:search% " +
            "and (:species is null or (speciesTags.tagKey='species (common)' and speciesTags.tagValue=:species)) " +
            "and (:source is null or (sourceTags.tagKey='sample source' and sourceTags.tagValue=:source)) " +
            "and (:disease is null or (diseaseTags.tagKey='disease' and diseaseTags.tagValue=:disease))")
    Page<SpectrumCluster> findClusters(@Param("search") String searchStr,
                                       @Param("species") String species,
                                       @Param("source") String source,
                                       @Param("disease") String disease,
                                       Pageable pageable);
}
