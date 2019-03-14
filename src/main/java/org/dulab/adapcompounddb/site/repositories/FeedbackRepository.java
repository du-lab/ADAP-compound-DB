package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query(value = "select f from Feedback f "
            + "where f.name like %:search%"
            + " or f.email like %:search%"
            + " or f.message like %:search%"
            + " or f.affiliation like %:search%")
    Page<Feedback> findAllFeedback(@Param("search") String searchStr, Pageable pageable);

}
