package org.dulab.site.services;

import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.models.UserParameters;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Validated
public interface SpectrumService {

    Optional<Spectrum> find(long id);

    List<Hit> match(@NotNull(message = "Query spectrum is required.") @Valid Spectrum querySpectrum,
                    @NotNull(message = "List of search criteria is required") CriteriaBlock criteria,
                    @NotNull(message = "Parameters are required.") @Valid UserParameters parameters);
}
