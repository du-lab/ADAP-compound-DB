package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.NamedQuery;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission,Long> {

    Iterable<Submission> findByUserId(long userPrincipalId);

    @Query(value = "select s from Submission s " + "where s.name like %:search%")
    Page<Submission> findAllSubmissions(@Param("search") String searchStr, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Submission s LEFT JOIN FETCH s.tags WHERE s.user.id = ?1")
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
    Iterable<BigInteger> findSubmissionIdsByUserAndSubmissionTags(@Param("userId") Long userId,
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

    
    @Query("select distinct s from Submission s " +
            "where s.user = :user and s.isPrivate = true and s.chromatographyType = :type and s.isReference = true")
    Iterable<Submission> findByPrivateTrueAndReferenceTrueAndUserAndChromatographyType(
            @Param("user") UserPrincipal user, @Param("type") ChromatographyType type);

    @Query("select distinct s from Submission s " +
            "where s.user = :user or s.user = :organization and s.isPrivate = true and s.chromatographyType = :type and s.isReference = true")
    Iterable<Submission> findByPrivateTrueAndReferenceTrueAndUserOrOrgAndChromatographyType(
            @Param("user") UserPrincipal user, @Param("organization") UserPrincipal userOrganization, @Param("type") ChromatographyType type);


    @Query("select distinct s from Submission s " +
            "where s.user = :user and s.isPrivate = true and s.isReference = true")
    Iterable<Submission> findByPrivateTrueAndReferenceTrueAndUser(@Param("user") UserPrincipal user);

    @Query("select distinct s.id, s.chromatographyType from Submission s where s.id in :ids")
    Iterable<Object[]> findChromatographyTypesBySubmissionId(@Param("ids") List<Long> submissionIds);


    Iterable<Submission> findByExternalId(String externalId);


    @Query("select distinct s from Submission s " + "where s.isPrivate = false and s.isReference = true")
    Iterable<Submission> findByPrivateFalseAndReferenceTrue();

    @Query("select distinct s from Submission s " +
            "where s.isPrivate = false and s.chromatographyType = :type and s.isReference= true")
    Iterable<Submission> findByPrivateFalseAndReferenceTrueAndChromatographyType(@Param("type") ChromatographyType type);

    @Query("select s.id, min(sp.reference) = 1 from Spectrum sp join sp.file.submission s where s.id in :ids group by s.id")
    Iterable<Object[]> findAnySpectrumReferenceBySubmissionIds(@Param("ids") List<Long> submissionIds);


    @Query(value = "select count(s.id) > 0 from Submission s join s.files f join f.spectra spectrum where " +
            "spectrum.chromatographyType in (org.dulab.adapcompounddb.models.enums.ChromatographyType.LC_MSMS_POS, " +
            "org.dulab.adapcompounddb.models.enums.ChromatographyType.LC_MSMS_NEG) and s.id = :id")
    boolean getIsSearchable(@Param("id") Long id);

    //get studies with inhousereference = false, clusterable = true, consensus = false
    @Query(value = "select distinct s from Submission s where s.clusterable = true "
            , nativeQuery = false)
    Page<Submission> findSubmissionByClusterableTrue(Pageable p);

    //update clusterable by submission ID
    @Modifying
    @Query(value = "update Submission s set s.clusterable=:clusterable where s.id=:submissionId")
    void updateClusterableBySubmissinoid(@Param("submissionId") long submissionId, @Param("clusterable") boolean clusterable);

    //get number of peaks by username
    @Query(value = "select count(s.id) from Submission s join s.user u join s.files f join f.spectra sp join sp.peaks where u.username= :userName")
    int getPeaksByUserName(@Param("userName") String userName);

    @Modifying
    @Query(value = "update Submission s set s.isReference=:isLibrary where s.id=:submissionId")
    void updateReferenceBySubmissionId(@Param("submissionId") long submissionId, @Param("isLibrary") boolean isLibrary);

    @Query("select distinct s.id, s.isReference from Submission s where s.id in :ids")
    Iterable<Object[]> getAllReferenceBySubmissionIds(@Param("ids") long[] submissionIds);

    @Query("select distinct s.id, s.isInHouseReference from Submission s where s.id in :ids")
    Iterable<Object[]> getAllInHouseReferenceBySubmissionIds(@Param("ids") long[] submissionIds);

    @Query("select distinct s.id, s.clusterable from Submission s where s.id in :ids")
    Iterable<Object[]> getAllClusterableBySubmissionIds(@Param("ids") long[] submissionIds);

    @Modifying
    @Query(value ="delete from Submission where userPrincipalId =:userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}
