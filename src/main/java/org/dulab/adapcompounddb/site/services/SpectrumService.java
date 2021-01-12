package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumService {

    Spectrum find(long id);

    DataTableResponse findSpectrumBySubmissionId(long submissionId, String search, int start, int length,
            String column, String direction);

    long countConsensusSpectra();

    long countReferenceSpectra();

    Boolean updateReferenceOfAllSpectraOfSubmission(Long submissionId, boolean value);
}
