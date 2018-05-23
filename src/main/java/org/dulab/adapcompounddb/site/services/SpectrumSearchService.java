package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.List;

public interface SpectrumSearchService {

    List<Hit> search(Spectrum spectrum, QueryParameters parameters);
}
