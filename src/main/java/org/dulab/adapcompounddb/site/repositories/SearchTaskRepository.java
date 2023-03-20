package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchTaskRepository extends JpaRepository<SearchTask, Long> {
}
