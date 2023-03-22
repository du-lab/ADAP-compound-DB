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

  @Query("Select st from SearchTask st  where st.submission.id =:submissionId and st.user.id =:userId")
  Optional<SearchTask> findByUserIdAndSubmissionId(@Param("userId") long id, @Param("submissionId") long submissionId);

  SearchTask findByUserIdAndSubmission(long id, Submission submission);
}
