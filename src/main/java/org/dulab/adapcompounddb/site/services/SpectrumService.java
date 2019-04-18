package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumService {

    Spectrum find(long id);

    DataTableResponse findSpectrumBySubmissionId(Long submissionId, String searchStr, Integer start, Integer length,
            Integer column, String orderDirection);

    List<Spectrum> findSpectrumBySubmissionId(Long submissionId);

    long countConsensusSpectra();

    long countReferenceSpectra();

    Boolean updateReferenceOfAllSpectraOfSubmission(Long submissionId, boolean value);

    DataTableResponse processPagination(Submission from, final String searchStr,
            final Integer start, final Integer length, final Integer column, final String orderDirection);
}
