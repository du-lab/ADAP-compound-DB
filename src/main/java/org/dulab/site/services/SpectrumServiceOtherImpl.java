package org.dulab.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.Hit;
import org.dulab.models.entities.Peak;
import org.dulab.models.entities.Spectrum;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.site.repositories.SpectrumRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Primary
public class SpectrumServiceOtherImpl implements SpectrumService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;

    public SpectrumServiceOtherImpl(SpectrumRepository spectrumRepository) {
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
    public List<Hit> match(Spectrum querySpectrum, CriteriaBlock criteria,
                           float mzTolerance, int numHits, float scoreThreshold) {

        Queue<Hit> topNumHits = new PriorityQueue<>(numHits, Comparator.comparingDouble(Hit::getScore));

        long startTime = System.currentTimeMillis();
        for (Spectrum spectrum : spectrumRepository.findAll()) {

            if (querySpectrum.equals(spectrum)) continue;

            float score = compare(querySpectrum, spectrum, mzTolerance);

            if (score < scoreThreshold) continue;

            Hit hit = new Hit();
            hit.setScore(score);
            hit.setSpectrum(spectrum);

            if (topNumHits.size() < numHits)
                topNumHits.add(hit);
            else if (topNumHits.peek().getScore() < score) {
                topNumHits.remove();
                topNumHits.add(hit);
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        LOGGER.info("Search of similar spectra in " + estimatedTime + " milliseconds.");

        List<Hit> hits = new ArrayList<>(topNumHits);
        hits.sort(Comparator.comparingDouble(h -> -h.getScore()));

        return hits;
    }

    private static float compare(Spectrum spectrum1, Spectrum spectrum2, float mzTolerance) {

        float sum = 0F;
        for (Peak peak1 : spectrum1.getPeaks())
            for (Peak peak2 : spectrum2.getPeaks())
                if (Math.abs(peak1.getMz() - peak2.getMz()) < mzTolerance)
                    sum += Math.sqrt(peak1.getIntensity() * peak2.getIntensity());

        return sum * sum;
    }
}
