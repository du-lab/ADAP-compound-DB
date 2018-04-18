package org.dulab.site.repositories;

import org.dulab.models.Hit;
import org.dulab.models.Spectrum;

public interface SpectrumRepositoryCustomization {

    Iterable<Hit> searchSpectra(Spectrum querySpectrum);
}
