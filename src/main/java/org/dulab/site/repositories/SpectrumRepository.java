package org.dulab.site.repositories;

import org.dulab.site.data.GenericRepository;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;

import java.util.List;

public interface SpectrumRepository extends GenericRepository<Long, Spectrum> {

    List<Hit> match(Spectrum spectrum);
}
