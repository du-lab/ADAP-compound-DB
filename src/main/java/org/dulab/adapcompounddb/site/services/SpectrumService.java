package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumService {

    Spectrum find(long id);

    DataTableResponse findSpectrumBySubmissionId(Long submissionId, String searchStr, Integer start, Integer length,
            Integer column, String orderDirection);

    long countConsensusSpectra();

    long countReferenceSpectra();

    Boolean updateReferenceOfAllSpectraOfSubmission(Long submissionId, boolean value);
}
