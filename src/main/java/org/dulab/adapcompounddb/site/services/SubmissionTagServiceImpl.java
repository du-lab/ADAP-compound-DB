package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionTagServiceImpl implements SubmissionTagService {

    private final SubmissionTagRepository submissionTagRepository;

    @Autowired
    public SubmissionTagServiceImpl(SubmissionTagRepository submissionTagRepository) {
        this.submissionTagRepository = submissionTagRepository;
    }

    @Override
    public List<String> findDistinctTagValuesByTagKey(String key) {
        List<String> values = ServiceUtils.toList(submissionTagRepository.findDistinctTagValuesByTagKey(key));
        values.sort(String.CASE_INSENSITIVE_ORDER);
        return values;
    }
}
