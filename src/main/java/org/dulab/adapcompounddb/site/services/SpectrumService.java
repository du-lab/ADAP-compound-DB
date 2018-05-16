package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface SpectrumService {

    Spectrum find(long id);

    List<Hit> match(@NotNull(message = "Query spectrum is required.") @Valid Spectrum querySpectrum,
                    @NotNull(message = "List of search criteria is required") CriteriaBlock criteria,
                    float mzTolerance, int numHits, float scoreTolerance);

    long getNumberOfSubmittedSpectra();
}
