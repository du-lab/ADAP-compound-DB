package org.dulab.adapcompounddb.site.repositories;

import java.util.Date;

import org.dulab.adapcompounddb.models.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query(value = "SELECT f FROM Feedback f "
            + " WHERE f.name like %:search% "
            + " OR f.email LIKE %:search% "
            + " OR f.message LIKE %:search% "
            + " OR f.affiliation LIKE %:search% ")
    Page<Feedback> findAllFeedback(@Param("search") String searchStr, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Feedback f "
            + " SET f.readFlag = True "
            + " WHERE f.id = :id")
    void markRead(@Param("id") Integer id);

    @Query("SELECT count(f) FROM Feedback f "
            + " WHERE year(f.submitDate) = year(:date) " +
            " and month(f.submitDate) = month(:date) " +
            " and day(f.submitDate) = day(:date) ")
    Integer getFeedbackCountOfDay(@Param("date") Date date);
}
