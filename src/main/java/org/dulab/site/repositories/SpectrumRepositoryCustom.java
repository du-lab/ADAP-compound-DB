package org.dulab.site.repositories;

import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.models.UserParameters;

public interface SpectrumRepositoryCustom {

    Iterable<Hit> searchSpectra(Spectrum querySpectrum, UserParameters parameters);
}
