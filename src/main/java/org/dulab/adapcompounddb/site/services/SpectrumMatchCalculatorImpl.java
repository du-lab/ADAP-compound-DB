package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SpectrumMatchCalculatorImpl implements SpectrumMatchCalculator {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;

    private final Map<ChromatographyType, QueryParameters> queryParametersMap;

    private float progress = 0F;

    @Autowired
    public SpectrumMatchCalculatorImpl(SpectrumRepository spectrumRepository,
                                       SpectrumMatchRepository spectrumMatchRepository) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;

        QueryParameters gcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.75)
                .setMzTolerance(0.01);

        QueryParameters lcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.75)
                .setMzTolerance(0.01)
                .setPrecursorTolerance(0.01)
                .setRetTimeTolerance(0.5);

        this.queryParametersMap = new HashMap<>();
        this.queryParametersMap.put(ChromatographyType.GAS, gcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LIQUID_POSITIVE, lcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LIQUID_NEGATIVE, lcQueryParameters);
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    @Transactional
    public void run() {

        List<SpectrumMatch> spectrumMatches = new ArrayList<>();

        final long countUnmatched = spectrumRepository.countUnmatched();

        progress = 0F;
        float progressStep = 1F / countUnmatched;

        for (ChromatographyType chromatographyType : ChromatographyType.values()) {

            QueryParameters params = queryParametersMap.get(chromatographyType);
            if (params == null)
                throw new IllegalStateException("Clustering query parameters are not specified.");

            LOGGER.info(String.format("Retrieving unmatched spectra of %s...", chromatographyType));
            Iterable<Spectrum> unmatchedSpectra =
                    spectrumRepository.findUnmatchedByChromatographyType(chromatographyType);

            long startingTime = System.currentTimeMillis();

            LOGGER.info(String.format("Matching unmatched spectra of %s...", chromatographyType));
            for (Spectrum querySpectrum : unmatchedSpectra) {
                spectrumMatches.addAll(
                        spectrumRepository.spectrumSearch(
                                SearchType.CLUSTERING, querySpectrum, params));
                progress += progressStep;
            }

            long elapsedTime = System.currentTimeMillis() - startingTime;
            LOGGER.info(String.format("Unmatched spectra of %s are matched with average time %d milliseconds.",
                    chromatographyType.getLabel(), countUnmatched > 0 ? elapsedTime / countUnmatched : 0));
        }

        LOGGER.info("Saving matches to the database...");
        spectrumMatchRepository.saveAll(spectrumMatches);
        progress = 0F;

        LOGGER.info(String.format("Total %d matches are saved to the database.", spectrumMatches.size()));
    }
}
