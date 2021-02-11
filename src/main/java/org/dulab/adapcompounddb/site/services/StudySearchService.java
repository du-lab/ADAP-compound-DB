package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.SubmissionMatchDTO;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;

import java.util.List;

public interface StudySearchService {

    List<SubmissionMatchDTO> studySearch(UserPrincipal user, Submission submission);
}
