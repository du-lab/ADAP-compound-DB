package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpectrumSearchServiceLCImpl implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public SpectrumSearchServiceLCImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum, parameters);
    }

    @Override
    public List<ClusterDTO> searchConsensusSpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance) {
        return null;
    }
}
