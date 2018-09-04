package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {

    Iterable<Submission> findByUserId(long userPrincipalId);

    @Query(value = "select s from Submission s " + "where s.name like %?1%")
    Page<Submission> findAllSubmissions(String searchStr, Pageable pageable);

    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.tags WHERE s.user.id = ?1")
    Iterable<Submission> findWithTagsByUserId(long userPrincipalId);

    void deleteById(long id);
}
