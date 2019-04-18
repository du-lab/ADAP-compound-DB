package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

public interface SpectrumSearchService {

    List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters);
}
