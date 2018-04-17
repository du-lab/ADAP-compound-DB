package org.dulab.site.services;

import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultSpectrumService implements SpectrumService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public DefaultSpectrumService(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public List<Hit> match(Spectrum querySpectrum) {
        return spectrumRepository.match(querySpectrum);
    }
}
