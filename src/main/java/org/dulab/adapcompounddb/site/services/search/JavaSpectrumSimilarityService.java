package org.dulab.adapcompounddb.site.services.search;

import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.stereotype.Service;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JavaSpectrumSimilarityService {

    private static final Logger LOGGER = LogManager.getLogger(JavaSpectrumSimilarityService.class);

    private final SpectrumRepository spectrumRepository;


    public JavaSpectrumSimilarityService(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    public List<SpectrumMatch> searchConsensusAndReference(
            Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user) {
        return search(querySpectrum, parameters, user, true, true, false);
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
        if (parameters.getSpecies() != null || parameters.getSource() != null || parameters.getDisease() != null)
            commonToSpectrumIdsMap = MappingUtils.toMapBigIntegerOfLists(
                    spectrumRepository.filterSpectra(commonToSpectrumIdsMap, parameters));

        List<BigInteger> preScreenedSpectrumIds =
                getSpectrumIdsWithCommonPeaksAboveThreshold(commonToSpectrumIdsMap, greedy ? Integer.MAX_VALUE : 50);
        Set<Long> preScreenedSpectrumIdsSet = preScreenedSpectrumIds.stream()
                .mapToLong(BigInteger::longValue)
                .boxed()
                .collect(Collectors.toSet());
        Iterable<Spectrum> preScreenedSpectra = spectrumRepository.findSpectraWithPeaksById(preScreenedSpectrumIdsSet);

        List<SpectrumMatch> matches = calculateSimilarity(querySpectrum, preScreenedSpectra, parameters);
        long time2 = System.currentTimeMillis();
        long timeCost = time2 - time1;
        try {

            File myFile = new File("/Users/ericliao/Desktop/compare_similarity_score_between_original_and_new/new study/threshold_50_8_15_time_cost.csv");
            if (myFile.createNewFile()){
                System.out.println("create time cost file");
                FileWriter writer = new FileWriter("/Users/ericliao/Desktop/compare_similarity_score_between_original_and_new/new study/threshold_50_8_15_time_cost.csv");
                writer.append("time");
                writer.append("\n");
                writer.close();
            } else {
                FileWriter writer = new FileWriter("/Users/ericliao/Desktop/compare_similarity_score_between_original_and_new/new study/threshold_50_8_15_time_cost.csv",true);
                writer.append(",");
                writer.append(Long.toString(timeCost));
                writer.append("\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ArrayList<>(matches.subList(0, Math.min(parameters.getLimit(), matches.size())));
    }

    private List<SpectrumMatch> calculateSimilarity(
            Spectrum querySpectrum, Iterable<Spectrum> librarySpectra, SearchParameters params) {

        List<SpectrumMatch> matches = new ArrayList<>();

        Double mzTolerance;
        boolean ppm;
        if (params.getMzTolerancePPM() != null) {
            mzTolerance = params.getMzTolerancePPM();
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
            if (mzTolerance != null && querySpectrum.getPeaks() != null && librarySpectrum.getPeaks() != null)
                similarityScore = calculateCosineSimilarity(
                        querySpectrum.getPeaks(), librarySpectrum.getPeaks(), mzTolerance, ppm);

            double massError = Double.MAX_VALUE;
            double massErrorPPM = Double.MAX_VALUE;
            if ((params.getMasses() != null || querySpectrum.getMass() != null)
                    && librarySpectrum.getMass() != null) {

                if (querySpectrum.getMass() != null) {
                    massError = Math.abs(querySpectrum.getMass() - librarySpectrum.getMass());
                    massErrorPPM = 1E6 * massError / librarySpectrum.getMass();
                } else {
                    massError = Arrays.stream(params.getMasses())
                            .map(mass -> Math.abs(mass - librarySpectrum.getMass()))
                            .min()
                            .orElse(Double.MAX_VALUE);
                    massErrorPPM = Arrays.stream(params.getMasses())
                            .map(mass -> 1E6 * Math.abs(mass - librarySpectrum.getMass()) / librarySpectrum.getMass())
                            .min()
                            .orElse(Double.MAX_VALUE);
                }
            }

            double retTimeError = Double.MAX_VALUE;
            if (querySpectrum.getRetentionTime() != null && librarySpectrum.getRetentionTime() != null)
                retTimeError = Math.abs(querySpectrum.getRetentionTime() - librarySpectrum.getRetentionTime());

            // if the similarity score > ScoreThreshold, then return the MatchSpectrum
            if ((params.getPrecursorTolerance() == null || precursorError < params.getPrecursorTolerance())
                    && (params.getPrecursorTolerancePPM() == null || precursorErrorPPM < params.getPrecursorTolerancePPM())
                    && (params.getScoreThreshold() == null || similarityScore > params.getScoreThreshold())
                    && (params.getMassTolerance() == null || massError < params.getMassTolerance())
                    && (params.getMassTolerancePPM() == null || massErrorPPM < params.getMassTolerancePPM())
                    && (params.getRetTimeTolerance() == null || retTimeError < params.getRetTimeTolerance())) {

                SpectrumMatch match = new SpectrumMatch();
                match.setQuerySpectrum(querySpectrum);
                match.setMatchSpectrum(librarySpectrum);
                match.setScore(similarityScore > 0 ? similarityScore : null);
                match.setMassError(massError < Double.MAX_VALUE ? massError : null);
                match.setMassErrorPPM(massErrorPPM < Double.MAX_VALUE ? massErrorPPM : null);
                match.setRetTimeError(retTimeError < Double.MAX_VALUE ? retTimeError : null);
                matches.add(match);
            }
        }

        matches.sort(Comparator.comparing(SpectrumMatch::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(SpectrumMatch::getMassError, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SpectrumMatch::getRetTimeError, Comparator.nullsLast(Comparator.naturalOrder())));

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

    private double calculateCosineSimilarity(List<Peak> queryPeaks, List<Peak> libraryPeaks,
                                             double tolerance, boolean ppm) {

        queryPeaks.sort(Comparator.comparingDouble(Peak::getMz));
        libraryPeaks.sort(Comparator.comparingDouble(Peak::getMz));

        double lowerFactor = 1.0 - 1E-6 * tolerance;
        double upperFactor = 1.0 + 1E-6 * tolerance;
        double dotProduct = 0.0;
        double queryNorm2 = 0.0;
        double libraryNorm2 = 0.0;
        int queryIndex = 0;
        int libraryIndex = 0;
        while (queryIndex < queryPeaks.size() || libraryIndex < libraryPeaks.size()) {
            if (queryIndex >= queryPeaks.size()) {
                double y = scale(libraryPeaks.get(libraryIndex));
                libraryNorm2 += y * y;
                libraryIndex++;
                continue;
            }
            if (libraryIndex >= libraryPeaks.size()) {
                double x = scale(queryPeaks.get(queryIndex));
                queryNorm2 += x * x;
                queryIndex++;
                continue;
            }
            Peak queryPeak = queryPeaks.get(queryIndex);
            Peak libraryPeak = libraryPeaks.get(libraryIndex);
            double queryMz = queryPeak.getMz();
            double libraryMz = libraryPeak.getMz();
            boolean queryMzLessThanLibraryMz = ppm ? queryMz < libraryMz * lowerFactor : queryMz < libraryMz - tolerance;
            boolean queryMzGreaterThanLibraryMz = ppm ? libraryMz * upperFactor < queryMz : queryMz > libraryMz + tolerance;
            if (queryMzLessThanLibraryMz) {
                double x = scale(queryPeak);
                queryNorm2 += x * x;
                queryIndex++;
            } else if (queryMzGreaterThanLibraryMz) {
                double y = scale(libraryPeak);
                libraryNorm2 += y * y;
                libraryIndex++;
            } else {  // queryMz and libraryMz are withing the tolerance
                double x = scale(queryPeak);
                double y = scale(libraryPeak);
                dotProduct += x * y;
                queryNorm2 += x * x;
                libraryNorm2 += y * y;
                queryIndex++;
                libraryIndex++;
            }
        }
        return dotProduct * dotProduct / (queryNorm2 * libraryNorm2);
    }

    private double scale(Peak peak) {
        return Math.pow(peak.getIntensity(), 0.5) * Math.pow(peak.getMz(), 0.0);
    }
}
