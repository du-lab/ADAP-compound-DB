package org.dulab.adapcompounddb.site.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectrumMatchCalculatorImpl implements SpectrumMatchCalculator {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;

    private final Map<ChromatographyType, QueryParameters> queryParametersMap;

    private float progress = -1F;

    @Autowired
    public SpectrumMatchCalculatorImpl(final SpectrumRepository spectrumRepository,
                                       final SpectrumMatchRepository spectrumMatchRepository) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;

        final QueryParameters gcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.5)
                .setMzTolerance(0.01);

        final QueryParameters lcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.75)
                .setMzTolerance(0.01)
                .setPrecursorTolerance(0.01);
        //                .setRetTimeTolerance(0.5);

        final QueryParameters massQueryParameters = new QueryParameters()
                .setMzTolerance(0.01);

        this.queryParametersMap = new HashMap<>();
        this.queryParametersMap.put(ChromatographyType.GAS, gcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LIQUID_POSITIVE, lcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LIQUID_NEGATIVE, lcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LC_MSMS_POS, lcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LC_MSMS_NEG, lcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.NONE, massQueryParameters);
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Override
    public void run() {

        final long countUnmatched = spectrumRepository.countUnmatched();

        progress = 0F;
        float progressStep = 0F;

        for (final ChromatographyType chromatographyType : ChromatographyType.values()) {

            final QueryParameters params = queryParametersMap.get(chromatographyType);
            if (params == null) {
                throw new IllegalStateException("Clustering query parameters are not specified.");
            }

            LOGGER.info(String.format("Retrieving unmatched spectra of %s...", chromatographyType));
            final Iterable<Spectrum> unmatchedSpectra = getUnmatchedSpectra(chromatographyType);
//                    spectrumRepository.findUnmatchedByChromatographyType(chromatographyType);

            LOGGER.info(String.format("Matching unmatched spectra of %s...", chromatographyType));
            long count = 0;
            long startingTime = System.currentTimeMillis();
            for (final Spectrum querySpectrum : unmatchedSpectra) {

//                spectrumMatches.addAll(spectrumRepository.spectrumSearch(
//                        SearchType.CLUSTERING, querySpectrum, params));

                if (chromatographyType == ChromatographyType.NONE) {
                    throw new IllegalStateException(String.format(
                            "Clustering of spectra of type %s is not currently supported", chromatographyType));
                }

                match(querySpectrum, params);
//                spectrumMatchRepository.saveAll(spectrumRepository.spectrumSearch(
//                        SearchType.CLUSTERING, querySpectrum, params));
//                spectrumMatchRepository.flush();

                progressStep = progressStep + 1F;
                progress = progressStep / countUnmatched;
                count += 1;

                if (count == 10) {
                    final long endingTime = System.currentTimeMillis();
                    LOGGER.info(String.format("%d spectra of %s are matched with average time %d milliseconds.",
                            count, chromatographyType.getLabel(), (endingTime - startingTime) / count));
                    count = 0;
                    startingTime = endingTime;
                }
            }

            final long elapsedTime = System.currentTimeMillis() - startingTime;
            LOGGER.info(String.format("%d spectra of %s are matched with average time %d milliseconds.",
                    count, chromatographyType.getLabel(), count > 0 ? elapsedTime / count : 0));
        }

        LOGGER.info("Saving matches to the database...");
//        spectrumMatchRepository.saveAll(spectrumMatches);
        progress = -1F;

        LOGGER.info("All matches are saved to the database.");
    }

    @Transactional
    public Iterable<Spectrum> getUnmatchedSpectra(ChromatographyType chromatographyType) {
        return spectrumRepository.findUnmatchedByChromatographyType(chromatographyType);
    }

    @Transactional
    @Async
    public void match(Spectrum querySpectrum, QueryParameters params) {
        try {
            List<SpectrumMatch> matches = spectrumRepository.spectrumSearch(
                    SearchType.CLUSTERING, querySpectrum, params);
            spectrumMatchRepository.saveAll(matches);
            spectrumMatchRepository.flush();
        } catch (Throwable t) {
            LOGGER.error("Error during spectrum matching: " + t.getMessage(), t);
            throw t;
        }
    }
}
