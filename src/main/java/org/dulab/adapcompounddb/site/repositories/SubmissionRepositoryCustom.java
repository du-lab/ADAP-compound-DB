package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Submission;

public interface SubmissionRepositoryCustom {

    Submission getSubmissionWithFilesSpectraPeaks(long submissionId);
}
