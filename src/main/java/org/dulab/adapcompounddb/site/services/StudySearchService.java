package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.SubmissionMatchDTO;
import org.dulab.adapcompounddb.models.entities.Submission;

import java.util.List;

public interface StudySearchService {

    List<SubmissionMatchDTO> studySearch(Submission submission);
}
