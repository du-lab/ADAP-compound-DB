package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.entities.Submission;

import javax.servlet.http.HttpSession;

public interface GroupSearchService {

    void groupSearch(long submissionId, HttpSession session, QueryParameters parameters);

    void nonSubmittedGroupSearch(Submission submission, HttpSession session, QueryParameters parameters);
}
