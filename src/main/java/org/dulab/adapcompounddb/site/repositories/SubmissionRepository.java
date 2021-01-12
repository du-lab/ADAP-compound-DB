package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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
            ") as SummaryTable " +
            "where (:species is null or :species='all' or Species=:species) " +
            "and (:source is null or :source='all' or Source=:source) " +
            "and (:disease is null or :disease='all' or Disease=:disease)",
            nativeQuery = true)
    Iterable<Long> findSubmissionIdsBySubmissionTags(
            @Param("species") String species, @Param("source") String source, @Param("disease") String disease);

}
