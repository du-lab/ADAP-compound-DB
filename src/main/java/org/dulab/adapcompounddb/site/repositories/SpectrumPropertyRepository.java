package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SpectrumPropertyRepository extends CrudRepository<SpectrumProperty, Long> {

    @Query("select p from SpectrumProperty p where p.spectrum.id in :spectrumIds")
    Iterable<SpectrumProperty> findBySpectrumId(@Param("spectrumIds") long[] spectrumIds);
}
