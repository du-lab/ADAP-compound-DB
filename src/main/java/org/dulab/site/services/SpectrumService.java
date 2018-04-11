package org.dulab.site.services;

import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface SpectrumService {

    List<Hit> match(@NotNull(message = "Query spectrum is requires") @Valid Spectrum querySpectrum);
}
