package org.dulab.site.repositories;

import org.dulab.models.Spectrum;
import org.springframework.data.repository.CrudRepository;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {
}
