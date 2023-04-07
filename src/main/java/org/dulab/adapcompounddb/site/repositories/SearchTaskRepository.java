package org.dulab.adapcompounddb.site.repositories;

import java.util.List;
import java.util.Optional;
import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface SearchTaskRepository extends JpaRepository<SearchTask, Long> {

  List<SearchTask> findByUserId(long id);

//  @Query(value = "select * from SearchTask where submissionId = :submissionId and userId = :userId", nativeQuery = true)
  Optional<SearchTask> findByUserIdAndSubmissionId(@Param("userId") long userId, @Param("submissionId") long submissionId);

}
