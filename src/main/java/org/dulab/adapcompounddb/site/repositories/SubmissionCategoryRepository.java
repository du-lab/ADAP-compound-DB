package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionCategoryRepository extends CrudRepository<SubmissionCategory, Long> {

    Iterable<SubmissionCategory> findAllByCategoryType(SubmissionCategoryType categoryType);

    @Query("SELECT size(c.submissions) FROM SubmissionCategory c WHERE c.id = :id")
    long countSubmissionsBySubmissionCategoryId(@Param("id") long id);
}
