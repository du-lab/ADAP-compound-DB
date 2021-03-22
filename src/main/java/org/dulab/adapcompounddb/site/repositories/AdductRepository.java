package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.data.repository.CrudRepository;

public interface AdductRepository extends CrudRepository<Adduct, Long> {

    Iterable<Adduct> findByChromatography(ChromatographyType chromatography);
}
