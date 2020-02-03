package org.dulab.adapcompounddb.site.services;

import java.util.List;

public interface SubmissionTagService {

    List<String> findDistinctTagValuesByTagKey(String key);
}
