package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.models.entities.SubmissionTagId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionTagRepository extends CrudRepository<SubmissionTag, SubmissionTagId> {

    @Query("SELECT DISTINCT t.id.name FROM SubmissionTag t")
    Iterable<String> findUniqueSubmissionTagNames();
}
