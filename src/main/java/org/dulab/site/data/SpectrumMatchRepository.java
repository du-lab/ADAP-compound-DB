package org.dulab.site.data;

import org.dulab.site.models.Spectrum;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface SpectrumMatchRepository {

    List<Spectrum> match(Spectrum querySpectrum);
}
