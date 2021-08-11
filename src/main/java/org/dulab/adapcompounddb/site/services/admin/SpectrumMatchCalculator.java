package org.dulab.adapcompounddb.site.services.admin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.site.services.search.JavaSpectrumSimilarityService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectrumMatchCalculator {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumMatchCalculator.class);

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SubmissionRepository submissionRepository;
    private final JavaSpectrumSimilarityService javaSpectrumSimilarityService;

//    private final Map<ChromatographyType, QueryParameters> queryParametersMap;

    private float progress = -1F;

    @Autowired
    public SpectrumMatchCalculator(SpectrumRepository spectrumRepository,
                                   SpectrumMatchRepository spectrumMatchRepository,
                                   SubmissionRepository submissionRepository,
                                   JavaSpectrumSimilarityService javaSpectrumSimilarityService) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.submissionRepository = submissionRepository;
        this.javaSpectrumSimilarityService = javaSpectrumSimilarityService;

//        final QueryParameters gcQueryParameters = new QueryParameters()
//                .setScoreThreshold(0.8)
//                .setMzTolerance(0.01);
//
//        final QueryParameters lcQueryParameters = new QueryParameters()
//                .setScoreThreshold(0.75)
//                .setMzTolerance(0.01)
//                .setPrecursorTolerance(0.01);
//        //                .setRetTimeTolerance(0.5);
//
//        final QueryParameters massQueryParameters = new QueryParameters()
//                .setMolecularWeightThreshold(0.01);
//
//        this.queryParametersMap = new HashMap<>();
//        this.queryParametersMap.put(ChromatographyType.GAS, gcQueryParameters);
//        this.queryParametersMap.put(ChromatographyType.LIQUID_POSITIVE, lcQueryParameters);
//        this.queryParametersMap.put(ChromatographyType.LIQUID_NEGATIVE, lcQueryParameters);
//        this.queryParametersMap.put(ChromatographyType.LC_MSMS_POS, lcQueryParameters);
//        this.queryParametersMap.put(ChromatographyType.LC_MSMS_NEG, lcQueryParameters);
//        this.queryParametersMap.put(ChromatographyType.NONE, massQueryParameters);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(final float progress) {
        this.progress = progress;
    }

    public void run() {

        final long countUnmatched =
                spectrumRepository.countByMatchesEmptyAndClusterableTrueAndConsensusFalseAndReferenceFalse();

//        final Iterable<BigInteger> qualifiedSubmissionIds = submissionRepository.findSubmissionIdsByUserAndSubmissionTags(
//                null, null, null, null);

        progress = 0F;
        float progressStep = 0F;

//        List<SpectrumMatch> spectrumMatches = new ArrayList<>();
        for (final ChromatographyType chromatographyType : ChromatographyType.values()) {

            LOGGER.info(String.format("Retrieving unmatched spectra of %s...", chromatographyType));
            final Iterable<Spectrum> unmatchedSpectra = getUnmatchedSpectra(chromatographyType);

            LOGGER.info(String.format("Matching unmatched spectra of %s...", chromatographyType));
            long count = 0;
            long startingTime = System.currentTimeMillis();
            for (final Spectrum querySpectrum : unmatchedSpectra) {

                if (chromatographyType == ChromatographyType.NONE) {
                    throw new IllegalStateException(String.format(
                            "Clustering of spectra of type %s is not currently supported", chromatographyType));
                }

                match(querySpectrum);

                progressStep = progressStep + 1F;
                progress = progressStep / countUnmatched;
                count += 1;

                if (count == 100) {
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
        return spectrumRepository.findUnmatchedSpectra(chromatographyType);
    }

    @Transactional
    @Async
    public List<SpectrumMatch> match(Spectrum querySpectrum) {

        SearchParameters params = getSearchParameters(querySpectrum.getChromatographyType(), querySpectrum.isIntegerMz());

        List<SpectrumMatch> matches;
        try {
//            List<SpectrumMatch> matches = MappingUtils.toList(spectrumRepository.matchAgainstClusterableSpectra(
//                    null, submissionIds, querySpectrum, params));
            matches = MappingUtils.toList(
                    javaSpectrumSimilarityService.searchClusterable(querySpectrum, params, null));
            spectrumMatchRepository.saveAll(matches);
            spectrumMatchRepository.flush();
        } catch (Throwable t) {
            LOGGER.error("Error during spectrum matching: " + t.getMessage(), t);
            throw t;
        }

        return matches;
    }

    private static SearchParameters getSearchParameters(ChromatographyType chromatographyType, boolean integerMz) {
        SearchParameters parameters = new SearchParameters();
        switch (chromatographyType) {
            case GAS:
            case LIQUID_POSITIVE:
            case LIQUID_NEGATIVE:
                parameters.setMzTolerance(0.01);
                parameters.setScoreThreshold(integerMz ? 0.8 : 0.4);
                parameters.setGreedy(false);
                parameters.setLimit(Integer.MAX_VALUE);
                break;
            case LC_MSMS_POS:
            case LC_MSMS_NEG:
                parameters.setMzTolerance(0.01);
                parameters.setScoreThreshold(0.4);
                parameters.setPrecursorTolerance(0.01);
                parameters.setGreedy(true);
                parameters.setLimit(Integer.MAX_VALUE);
                break;
            case NONE:
                parameters.setMassTolerance(0.01);
                parameters.setGreedy(true);
                parameters.setLimit(Integer.MAX_VALUE);
                break;
        }
        return parameters;
    }
}
