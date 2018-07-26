package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumService {

    Spectrum find(long id);

	List<SpectrumDTO> findSpectrumBySubmissionId(Long submissionId, int start, int length, String column, String order);
}
