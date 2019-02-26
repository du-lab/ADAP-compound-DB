package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
