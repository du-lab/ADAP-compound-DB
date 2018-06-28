package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpectrumSearchServiceImpl implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public SpectrumSearchServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    public List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters) {

        List<SpectrumMatch> allHits = new ArrayList<>();

//        if (spectrum.getRetentionTime() != null && spectrum.getPrecursor() != null) {
//            List<Hit> hits = spectrumRepository.retTimePrecursorMsMsSearch(spectrum, parameters);
//            allHits.addAll(hits);
//            parameters.addExludeSpectra(hitsToSpectra(hits));
//        }
//
//        if (spectrum.getRetentionTime() != null && spectrum.getPrecursor() != null) {
//            List<Hit> hits = spectrumRepository.retTimePrecursorSearch(spectrum, parameters);
//            allHits.addAll(hits);
//            parameters.addExludeSpectra(hitsToSpectra(hits));
//        }
//
//        if (spectrum.getPrecursor() != null) {
//            List<Hit> hits = spectrumRepository.precursorMsMsSearch(spectrum, parameters);
//            allHits.addAll(hits);
//            parameters.addExludeSpectra(hitsToSpectra(hits));
//        }
//
//        if (spectrum.getPrecursor() != null) {
//            List<Hit> hits = spectrumRepository.precursorSearch(spectrum, parameters);
//            allHits.addAll(hits);
//            parameters.addExludeSpectra(hitsToSpectra(hits));
//        }

        return allHits;
    }

    private Set<Spectrum> hitsToSpectra(List<Hit> hits) {
        return hits.stream()
                .map(Hit::getSpectrum)
                .distinct()
                .collect(Collectors.toSet());
    }
}
