package org.dulab.adapcompounddb.site.services.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.repositories.MultiFetchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.RetIndexMatchType;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JavaSpectrumSimilarityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSpectrumSimilarityService.class);

    private static final double RET_INDEX_STRONG_PENALTY = 0.5;
    private static final double RET_INDEX_AVERAGE_PENALTY = 0.7;
    private static final double RET_INDEX_WEAK_PENALTY = 0.9;

    private final SpectrumRepository spectrumRepository;
    private final MultiFetchRepository multiFetchRepository;

    private final LinkedHashMap<Long, Spectrum> spectrumCache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Spectrum> eldest) {
            return size() > 1000;
        }
    };


    public JavaSpectrumSimilarityService(SpectrumRepository spectrumRepository,
                                         MultiFetchRepository multiFetchRepository) {
        this.spectrumRepository = spectrumRepository;
        this.multiFetchRepository = multiFetchRepository;
    }

    public List<SpectrumMatch> searchConsensusAndReference(
            Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user) {

        Set<BigInteger> submissionIds = parameters.getSubmissionIds();

        boolean searchConsensus = submissionIds == null || submissionIds.contains(BigInteger.ZERO);
        boolean searchReference = submissionIds == null
                || submissionIds.stream().anyMatch(id -> !Objects.equals(id, BigInteger.ZERO));

        if (!searchConsensus && !searchReference) {
            LOGGER.info("Spectrum search is attempted without any selected libraries");
            return new ArrayList<>(0);
        }

        return search(querySpectrum, parameters, user, searchConsensus, searchReference, false);
    }

    public List<SpectrumMatch> searchClusterable(
            Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user) {
        return search(querySpectrum, parameters, user, false, false, true);
    }

    public List<SpectrumMatch> search(Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user,
                                      boolean searchConsensus, boolean searchReference, boolean searchClusterable) {

        boolean greedy;
        if (parameters.getGreedy() != null)
            greedy = parameters.getGreedy();
        else
            greedy = querySpectrum.getChromatographyType() == ChromatographyType.LC_MSMS_POS
                    || querySpectrum.getChromatographyType() == ChromatographyType.LC_MSMS_NEG;

        long time1 = System.currentTimeMillis();
        Map<BigInteger, List<BigInteger>> commonToSpectrumIdsMap = MappingUtils.toMapBigIntegerOfLists(
                spectrumRepository.preScreenSpectra(querySpectrum, parameters, user, greedy,
                        searchConsensus, searchReference, searchClusterable));
        long time2 = System.currentTimeMillis();
        double preScreenTime = (time2 - time1) / 1000.0;

        if (commonToSpectrumIdsMap.isEmpty())
            return new ArrayList<>(0);

        if (parameters.getSpecies() != null || parameters.getSource() != null || parameters.getDisease() != null)
            commonToSpectrumIdsMap = MappingUtils.toMapBigIntegerOfLists(
                    spectrumRepository.filterSpectra(commonToSpectrumIdsMap, parameters));

        List<BigInteger> preScreenedSpectrumIds =
                getSpectrumIdsWithCommonPeaksAboveThreshold(commonToSpectrumIdsMap, greedy ? Integer.MAX_VALUE : 50);

        Set<Long> preScreenedSpectrumIdsSet = preScreenedSpectrumIds.stream()
                .mapToLong(BigInteger::longValue)
                .boxed()
                .collect(Collectors.toSet());

        // Fetch spectra from the cache or the database
        List<Spectrum> preScreenedSpectra = new ArrayList<>();
        Set<Long> uncachedSpectrumIds = new HashSet<>();
        for (Long spectrumId : preScreenedSpectrumIdsSet) {
            Spectrum spectrum = spectrumCache.get(spectrumId);
            if (spectrum != null) {
                preScreenedSpectra.add(spectrum);
            } else {
                uncachedSpectrumIds.add(spectrumId);
            }
        }

        long time3 = System.currentTimeMillis();
        if (!uncachedSpectrumIds.isEmpty()) {
            Iterable<Spectrum> spectra =
                    multiFetchRepository.getSpectraWithPeaksIsotopes(uncachedSpectrumIds);
            for (Spectrum spectrum : spectra) {
                preScreenedSpectra.add(spectrum);
                spectrumCache.put(spectrum.getId(), spectrum);
            }
        }
        long time4 = System.currentTimeMillis();
        double fetchSpectraTime = (time4 - time3) / 1000.0;

        long time5 = System.currentTimeMillis();
        List<SpectrumMatch> matches = calculateSimilarity(querySpectrum, preScreenedSpectra, parameters);
        long time6 = System.currentTimeMillis();
        double similarityTime = (time6 - time5) / 1000.0;

        double totalTime = (time6 - time1) / 1000.0;
        LOGGER.info(String.format("Pre-screen: %.2f; Fetch (%d): %.2f; Similarity: %.2f; Total: %.2f",
                preScreenTime, preScreenedSpectrumIdsSet.size(), fetchSpectraTime, similarityTime, totalTime));

        return new ArrayList<>(matches.subList(0, Math.min(parameters.getLimit(), matches.size())));
    }

    private List<SpectrumMatch> calculateSimilarity(
            Spectrum querySpectrum, Iterable<Spectrum> librarySpectra, SearchParameters params) {

        List<SpectrumMatch> matches = new ArrayList<>();


        double mzTolerance;
        boolean ppm;
        if (params.getMzTolerancePPM() != null) {
            mzTolerance = params.getMzTolerancePPM().doubleValue();
            ppm = true;
        } else {
            mzTolerance = params.getMzTolerance();
            ppm = false;
        }

        // iterate each spectrum in the adap-kdb library
        for (Spectrum librarySpectrum : librarySpectra) {
            double precursorError = Double.MAX_VALUE;
            double precursorErrorPPM = Double.MAX_VALUE;
            if (querySpectrum.getPrecursor() != null && librarySpectrum.getPrecursor() != null) {
                precursorError = Math.abs(querySpectrum.getPrecursor() - librarySpectrum.getPrecursor());
                precursorErrorPPM = 1E6 * precursorError / librarySpectrum.getPrecursor();
            }

            double similarityScore = 0.0;
//            List<Double> queryPeakMzs = new ArrayList<>();
//            List<Double> libraryPeakMzs = new ArrayList<>();
            if (querySpectrum.getPeaks() != null && librarySpectrum.getPeaks() != null)  // mzTolerance != null &&
                similarityScore = calculateCosineSimilarity(querySpectrum, librarySpectrum,
                        mzTolerance, ppm, params.isPenalizeQueryImpurities(), params.isPenalizeDominantPeak());  // , queryPeakMzs, libraryPeakMzs

//            System.out.printf("%s %s %f%n", querySpectrum.getName(), librarySpectrum.getName(), similarityScore);

            double[] queryIsotopes = querySpectrum.getIsotopesAsArray();
            double isotopicSimilarity = (queryIsotopes != null)
                    ? calculateCosineSimilarity(queryIsotopes, librarySpectrum.getIsotopesAsArray())
                    : 0.0;

            double massError = Double.MAX_VALUE;
            double massErrorPPM = Double.MAX_VALUE;
            String precursorType = null;
            if ((params.getAdducts() != null || querySpectrum.getMass() != null)
                    && librarySpectrum.getMass() != null) {

                if (querySpectrum.getMass() != null) {
                    massError = Math.abs(querySpectrum.getMass() - librarySpectrum.getMass());
                    massErrorPPM = 1E6 * massError / librarySpectrum.getMass();
                    precursorType = querySpectrum.getPrecursorType();
                } else {
                    for (Adduct adduct : params.getAdducts()) {
                        double massDifference = Math.abs(
                                adduct.calculateNeutralMass(querySpectrum.getPrecursor()) - librarySpectrum.getMass());
                        if (massDifference < massError) {
                            massError = massDifference;
                            precursorType = adduct.getName();
                        }

                        double ppmDifference = 1E6 * massDifference / librarySpectrum.getMass();
                        if (ppmDifference < massErrorPPM) {
                            massErrorPPM = ppmDifference;
                            precursorType = adduct.getName();
                        }
                    }
//                    params.getAdducts().stream()
//                            .collect(Collectors.)
//                    massError = Arrays.stream(params.getMasses())
//                            .map(mass -> Math.abs(mass - librarySpectrum.getMass()))
//                            .min()
//                            .orElse(Double.MAX_VALUE);
//                    massErrorPPM = Arrays.stream(params.getMasses())
//                            .map(mass -> 1E6 * Math.abs(mass - librarySpectrum.getMass()) / librarySpectrum.getMass())
//                            .min()
//                            .orElse(Double.MAX_VALUE);
                }
            }

            double retTimeError = Double.MAX_VALUE;
            if (querySpectrum.getRetentionTime() != null && librarySpectrum.getRetentionTime() != null)
                retTimeError = Math.abs(querySpectrum.getRetentionTime() - librarySpectrum.getRetentionTime());

            double retIndexError = Double.MAX_VALUE;
            if (querySpectrum.getRetentionIndex() != null && librarySpectrum.getRetentionIndex() != null)
                retIndexError = Math.abs(querySpectrum.getRetentionIndex() - librarySpectrum.getRetentionIndex());

            // if the similarity score > ScoreThreshold, then return the MatchSpectrum
            if ((params.getPrecursorTolerance() == null || precursorError < params.getPrecursorTolerance())
                    && (params.getPrecursorTolerancePPM() == null || precursorErrorPPM < params.getPrecursorTolerancePPM())
                    && (params.getScoreThreshold() == null || similarityScore > params.getScoreThreshold())
                    && (params.getMassTolerance() == null || massError < params.getMassTolerance())
                    && (params.getMassTolerancePPM() == null || massErrorPPM < params.getMassTolerancePPM())
                    && (params.getRetTimeTolerance() == null || retTimeError < params.getRetTimeTolerance())
                    && (params.getIsotopicSimilarityThreshold() == null || isotopicSimilarity > params.getIsotopicSimilarityThreshold())
                    && (params.getRetIndexTolerance() == null || params.getRetIndexMatchType() != RetIndexMatchType.ALWAYS_MATCH || retIndexError < params.getRetIndexTolerance())) {

                SpectrumMatch match = new SpectrumMatch();
                match.setQuerySpectrum(querySpectrum);
                match.setMatchSpectrum(librarySpectrum);
                match.setScore(similarityScore > 0 ? similarityScore : null);
                match.setIsotopicSimilarity(isotopicSimilarity > 0 ? isotopicSimilarity : null);
                match.setPrecursorError(precursorError < Double.MAX_VALUE ? precursorError : null);
                match.setPrecursorErrorPPM(precursorErrorPPM < Double.MAX_VALUE ? precursorErrorPPM : null);
                match.setPrecursorType(precursorType);
                match.setMassError(massError < Double.MAX_VALUE ? massError : null);
                match.setMassErrorPPM(massErrorPPM < Double.MAX_VALUE ? massErrorPPM : null);
                match.setRetTimeError(retTimeError < Double.MAX_VALUE ? retTimeError : null);
                match.setRetIndexError(retIndexError < Double.MAX_VALUE ? retIndexError : null);
//                match.setQueryPeakMzs(queryPeakMzs);
//                match.setLibraryPeakMzs(libraryPeakMzs);

                if (params.getRetIndexTolerance() != null && retIndexError >= params.getRetIndexTolerance()) {

                    if (params.getRetIndexMatchType() == RetIndexMatchType.ALWAYS_MATCH)
                        continue;

                    Double score = match.getScore();
                    if (score != null) {
                        if (params.getRetIndexMatchType() == RetIndexMatchType.PENALIZE_NO_MATCH_STRONG)
                            match.setScore(RET_INDEX_STRONG_PENALTY * score);
                        else if (params.getRetIndexMatchType() == RetIndexMatchType.PENALIZE_NO_MATCH_AVERAGE)
                            match.setScore(RET_INDEX_AVERAGE_PENALTY * score);
                        else if (params.getRetIndexMatchType() == RetIndexMatchType.PENALIZE_NO_MATCH_WEAK)
                            match.setScore(RET_INDEX_WEAK_PENALTY * score);

                        if (params.getScoreThreshold() != null && match.getScore() <= params.getScoreThreshold())
                            continue;
                    }
                }

                matches.add(match);
            }
        }

        matches.sort(Comparator.comparing(SpectrumMatch::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(SpectrumMatch::getMassError, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SpectrumMatch::getRetTimeError, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SpectrumMatch::getRetIndexError, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SpectrumMatch::getIsotopicSimilarity, Comparator.nullsLast(Comparator.reverseOrder())));

        return matches;
    }

    private List<BigInteger> getSpectrumIdsWithCommonPeaksAboveThreshold(
            Map<BigInteger, List<BigInteger>> commonToSpectrumIdsMap, long threshold) {

        List<BigInteger> spectraList = new ArrayList<>();
        for (BigInteger i = BigInteger.valueOf(16); i.compareTo(BigInteger.ZERO) > 0; i = i.subtract(BigInteger.ONE)) {
            List<BigInteger> spectra = commonToSpectrumIdsMap.get(i);
            if (spectra != null) {
                spectraList.addAll(commonToSpectrumIdsMap.get(i));
                if (spectraList.size() > threshold) {
                    break;
                }
            }
        }
        return spectraList;
    }

    private List<Peak> filterPeaks(Spectrum spectrum, double tolerance, boolean ppm) {
        if (spectrum == null) return null;

        List<Peak> peaks = spectrum.getPeaks();
        if (peaks == null) return null;

        if (spectrum.getPrecursor() != null) {
            double precursor = spectrum.getPrecursor();
            double lowerBoundary = getLowerBoundary(precursor, tolerance, ppm);
            double upperBoundary = getUpperBoundary(precursor, tolerance, ppm);
            peaks = peaks.stream()
                    .filter(p -> p.getMz() < lowerBoundary || p.getMz() > upperBoundary)
                    .collect(Collectors.toList());
        }

        double maxIntensity = peaks.stream().mapToDouble(Peak::getIntensity).max().orElse(0.0);
        return peaks.stream()
                .filter(p -> p.getIntensity() >= 0.025 * maxIntensity)
                .collect(Collectors.toList());
    }

    private double getLowerBoundary(double mz, double tolerance, boolean ppm) {
        return ppm ? mz * (1.0 - 1E-6 * tolerance) : mz - tolerance;
    }

    private double getUpperBoundary(double mz, double tolerance, boolean ppm) {
        return ppm ? mz * (1.0 + 1E-6 * tolerance) : mz + tolerance;
    }

    private double calculateCosineSimilarity(Spectrum querySpectrum, Spectrum librarySpectrum,
                                             double tolerance, boolean ppm,
                                             boolean penalizeQueryImpurities, boolean penalizeDominantPeak
                                             ) {  // List<Double> queryPeakMzs, List<Double> libraryPeakMzs

        List<Peak> queryPeaks = filterPeaks(querySpectrum, tolerance, ppm);
        List<Peak> libraryPeaks = filterPeaks(librarySpectrum, tolerance, ppm);

        double queryOmegaFactor = penalizeDominantPeak ? querySpectrum.getOmegaFactor() : 0.0;
        double libraryOmegaFactor = penalizeDominantPeak ? librarySpectrum.getOmegaFactor() : 0.0;

        queryPeaks.sort(Comparator.comparingDouble(Peak::getMz));
        libraryPeaks.sort(Comparator.comparingDouble(Peak::getMz));

//        double lowerFactor = 1.0 - 1E-6 * tolerance;
//        double upperFactor = 1.0 + 1E-6 * tolerance;

        double dotProduct = 0.0;
        double queryNorm2 = 0.0;
        double libraryNorm2 = 0.0;

        int queryIndex = 0;
        int libraryIndex = 0;
        while (queryIndex < queryPeaks.size() || libraryIndex < libraryPeaks.size()) {

            if (queryIndex >= queryPeaks.size()) {
                double y = scale(libraryPeaks.get(libraryIndex), libraryOmegaFactor);
                libraryNorm2 += y * y;
                libraryIndex++;
                continue;
            }

            if (libraryIndex >= libraryPeaks.size()) {
                if (penalizeQueryImpurities) {
                    double x = scale(queryPeaks.get(queryIndex), queryOmegaFactor);
                    queryNorm2 += x * x;
                }
                queryIndex++;
                continue;
            }

            Peak queryPeak = queryPeaks.get(queryIndex);
            Peak libraryPeak = libraryPeaks.get(libraryIndex);

            double queryMz = queryPeak.getMz();
            double libraryMz = libraryPeak.getMz();

//            boolean queryMzLessThanLibraryMz = ppm ? queryMz < libraryMz * lowerFactor : queryMz < libraryMz - tolerance;
//            boolean queryMzGreaterThanLibraryMz = ppm ? libraryMz * upperFactor < queryMz : queryMz > libraryMz + tolerance;
            boolean queryMzLessThanLibraryMz = queryMz < getLowerBoundary(libraryMz, tolerance, ppm);
            boolean queryMzGreaterThanLibraryMz = queryMz > getUpperBoundary(libraryMz, tolerance, ppm);

            if (queryMzLessThanLibraryMz) {
                if (penalizeQueryImpurities) {
                    double x = scale(queryPeak, queryOmegaFactor);
                    queryNorm2 += x * x;
                }
                queryIndex++;

            } else if (queryMzGreaterThanLibraryMz) {
                double y = scale(libraryPeak, libraryOmegaFactor);
                libraryNorm2 += y * y;
                libraryIndex++;

            } else {  // queryMz and libraryMz are withing the tolerance
                double x = scale(queryPeak, queryOmegaFactor);
                double y = scale(libraryPeak, libraryOmegaFactor);

//                //TODO: store querypeakmz and library peak mz
//                queryPeakMzs.add(queryPeak.getMz());
//                libraryPeakMzs.add(libraryPeak.getMz());

                dotProduct += x * y;
                queryNorm2 += x * x;
                libraryNorm2 += y * y;
                queryIndex++;
                libraryIndex++;
            }
        }

        return dotProduct * dotProduct / (queryNorm2 * libraryNorm2);
    }

    private double calculateCosineSimilarity(double[] values1, double[] values2) {
        if (values1 == null || values2 == null) return 0.0;

        int length = Math.max(values1.length, values2.length);
        if (length == 0) return 0.0;

        double dotProduct = 0.0;
        double normSquare1 = 0.0;
        double normSquare2 = 0.0;

        for (int i = 0; i < length; ++i) {
            if (i < values1.length && i < values2.length) {
                dotProduct += values1[i] * values2[i];
                normSquare1 += values1[i] * values1[i];
                normSquare2 += values2[i] * values2[i];
            } else if (i < values1.length) {
                normSquare1 += values1[i] * values1[i];
            } else if (i < values2.length) {
                normSquare2 += values2[i] * values2[i];
            }
        }

        return dotProduct / Math.sqrt(normSquare1 * normSquare2);
    }

    private double scale(Peak peak, double omegaFactor) {
        return Math.pow(peak.getIntensity() * peak.getMz() / (1.0 + omegaFactor * peak.getIntensity()), 0.5);
    }
}
