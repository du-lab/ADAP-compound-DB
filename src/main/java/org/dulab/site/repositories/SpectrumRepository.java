package org.dulab.site.repositories;

import org.dulab.models.entities.Spectrum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY")
    Iterable<Spectrum> findAllByMatchesIsEmpty();

    long countByConsensusIsFalse();
}
