package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GCSpectrumSearchServiceImpl implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public GCSpectrumSearchServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum, parameters);
    }
}
