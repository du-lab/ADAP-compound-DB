package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {

    Iterable<Submission> findByUserId(long userPrincipalId);

    @Query(value = "select s from Submission s " + "where s.name like %:search%")
    Page<Submission> findAllSubmissions(@Param("search") String searchStr, Pageable pageable);

    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.tags WHERE s.user.id = ?1")
    Iterable<Submission> findWithTagsByUserId(long userPrincipalId);

    void deleteById(long id);

    @Query(value = "select Id from (" +
            "select Submission.Id, SpeciesTag.TagValue as Species, SourceTag.TagValue as Source, DiseaseTag.TagValue as Disease from Submission " +
            "left join (select SubmissionId, TagValue from SubmissionTag where TagKey='species (common)') as SpeciesTag on SpeciesTag.SubmissionId=Submission.Id " +
            "left join (select SubmissionId, TagValue from SubmissionTag where TagKey='sample source') as SourceTag on SourceTag.SubmissionId=Submission.Id " +
            "left join (select SubmissionId, TagValue from SubmissionTag where TagKey='disease') as DiseaseTag on DiseaseTag.SubmissionId=Submission.Id " +
            "where Submission.IsPrivate is false or (Submission.IsPrivate is true and Submission.UserPrincipalId = :userId) " +
            ") as SummaryTable " +
            "where (:species is null or :species='all' or Species=:species) " +
            "and (:source is null or :source='all' or Source=:source) " +
            "and (:disease is null or :disease='all' or Disease=:disease)",
            nativeQuery = true)
    Iterable<BigInteger> findSubmissionIdsBySubmissionTags(@Param("userId") Long userId,
                                                           @Param("species") String species, @Param("source") String source, @Param("disease") String disease);

    @Query(value = "select Id from (" +
            "select Submission.Id, SpeciesTag.TagValue as Species, SourceTag.TagValue as Source, DiseaseTag.TagValue as Disease from Submission " +
            "left join (select SubmissionId, TagValue from SubmissionTag where TagKey='species (common)') as SpeciesTag on SpeciesTag.SubmissionId=Submission.Id " +
            "left join (select SubmissionId, TagValue from SubmissionTag where TagKey='sample source') as SourceTag on SourceTag.SubmissionId=Submission.Id " +
            "left join (select SubmissionId, TagValue from SubmissionTag where TagKey='disease') as DiseaseTag on DiseaseTag.SubmissionId=Submission.Id " +
            "where Submission.IsPrivate is false " +
            ") as SummaryTable " +
            "where (:species is null or :species='all' or Species=:species) " +
            "and (:source is null or :source='all' or Source=:source) " +
            "and (:disease is null or :disease='all' or Disease=:disease)",
            nativeQuery = true)
    Iterable<BigInteger> findSubmissionIdsBySubmissionTags(
            @Param("species") String species, @Param("source") String source, @Param("disease") String disease);

    @Query("select distinct s from Submission s join s.files f join f.spectra spectrum left join fetch s.tags where spectrum.id in (:spectrumIds)")
    Iterable<Submission> findSubmissionsWithTagsBySpectrumId(@Param("spectrumIds") Set<Long> spectrumIds);

    @Query("select distinct s from Spectrum sp join sp.file.submission s " +
            "where s.user = :user and s.isPrivate = true and sp.chromatographyType = :type and sp.reference = true")
    Iterable<Submission> findByPrivateTrueAndReferenceTrueAndUserAndChromatographyType(
            @Param("user") UserPrincipal user, @Param("type") ChromatographyType type);

    @Query("select distinct s from Spectrum sp join sp.file.submission s " +
            "where s.user = :user and s.isPrivate = true and sp.reference = true")
    Iterable<Submission> findByPrivateTrueAndReferenceTrueAndUser(@Param("user") UserPrincipal user);

    @Query("select distinct s.id, sp.chromatographyType from Spectrum sp join sp.file.submission s where s.id in :ids")
    Iterable<Object[]> findChromatographyTypesBySubmissionId(@Param("ids") List<Long> submissionIds);
}
