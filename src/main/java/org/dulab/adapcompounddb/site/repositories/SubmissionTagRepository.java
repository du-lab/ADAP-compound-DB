package org.dulab.adapcompounddb.site.repositories;

import java.util.List;

import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.models.entities.SubmissionTagId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubmissionTagRepository extends CrudRepository<SubmissionTag, SubmissionTagId> {

    @Query("SELECT DISTINCT t.id.name FROM SubmissionTag t")
    Iterable<String> findUniqueSubmissionTagNames();

    @Query("SELECT DISTINCT s.id, tag.id.name from Spectrum sp, SubmissionTag tag "
            + "inner join tag.id.submission s "
            + "inner join sp.file f "
            + "where sp.cluster.id = :clusterId "
            + "and f.submission.id = s.id")
    List<String> findTagsFromACluster(@Param("clusterId") Long clusterId);
}
