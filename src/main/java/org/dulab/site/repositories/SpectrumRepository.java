package org.dulab.site.repositories;

import org.dulab.site.data.GenericRepository;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long> {

    List<Hit> match(Spectrum spectrum);
}
