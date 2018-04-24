package org.dulab.site.services;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.models.UserParameters;
import org.dulab.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpectrumServiceImpl implements SpectrumService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public SpectrumServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public Spectrum find(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(EmptySearchResultException::new);
    }

    @Override
    @Transactional
    public List<Hit> match(Spectrum querySpectrum, CriteriaBlock criteria, UserParameters parameters)
            throws EmptySearchResultException {

        List<Hit> hits = ServiceUtils.toList(spectrumRepository.searchSpectra(querySpectrum, criteria, parameters));

        if (hits.isEmpty())
            throw new EmptySearchResultException();

        return hits;
    }
}
