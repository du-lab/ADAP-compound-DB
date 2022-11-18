package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionTagRepository extends CrudRepository<SubmissionTag, Long> {

    @Query("SELECT DISTINCT CONCAT(t.tagKey, ' " + SubmissionTag.TAG_DELIMITER + " ', t.tagValue) FROM SubmissionTag t")
    Iterable<String> findUniqueTagStrings();

    @Query("select distinct t.tagValue from SubmissionTag t where t.tagKey=:key")
    Iterable<String> findDistinctTagValuesByTagKey(@Param("key") String key);

//    @Query("SELECT distinct f.submission.id, tag.key, tag.value from Spectrum sp, SubmissionTag tag "
//            + "inner join tag.submission s "
//            + "inner join sp.file f "
//            + "where sp.cluster.id = :clusterId "
//            + "and f.submission.id = s.id")
//    List<Object[]> findTagsFromACluster(@Param("clusterId") Long clusterId);
}
