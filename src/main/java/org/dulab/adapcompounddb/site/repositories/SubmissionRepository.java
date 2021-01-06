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

    @Query(value = "select distinct * from (select SubmissionId from SubmissionTag " +
            "where :species is null or :species='all' or (TagKey='species (common)' and TagValue=:species)) as SpeciesTag " +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :source is null or :source='all' or (TagKey='sample source' and TagValue=:source)) as SourceTag " +
            "using (SubmissionId)" +
            "inner join (select SubmissionId from SubmissionTag " +
            "where :disease is null or :disease='all' or (TagKey='disease' and TagValue=:disease)) as DiseaseTag " +
            "using (SubmissionId)",
            nativeQuery = true)
    Iterable<Long> findSubmissionIdsBySubmissionTags(
            @Param("species") String species, @Param("source") String source, @Param("disease") String disease);

}
