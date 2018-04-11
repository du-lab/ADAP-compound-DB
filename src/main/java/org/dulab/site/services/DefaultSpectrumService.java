package org.dulab.site.services;

import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.site.repositories.DefaultSpectrumRepository;
import org.dulab.site.repositories.SpectrumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultSpectrumService implements SpectrumService {

    private SpectrumRepository spectrumRepository;

    public DefaultSpectrumService() {
        spectrumRepository = new DefaultSpectrumRepository();
    }

    @Override
    @Transactional
    public List<Hit> match(Spectrum querySpectrum) {
        return spectrumRepository.match(querySpectrum);
    }
}
