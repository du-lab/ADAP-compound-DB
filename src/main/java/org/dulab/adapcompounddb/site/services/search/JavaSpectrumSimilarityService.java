package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JavaSpectrumSimilarityService {

    private final SpectrumRepository spectrumRepository;


    public JavaSpectrumSimilarityService(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    public List<SpectrumMatch> searchConsensusAndReference(Spectrum querySpectrum, SearchParameters parameters) {
        return search(querySpectrum, parameters, true, true, false);
    }

    public List<SpectrumMatch> searchClusterable(Spectrum querySpectrum, SearchParameters parameters) {
        return search(querySpectrum, parameters, false, false, true);
    }

    public List<SpectrumMatch> search(Spectrum querySpectrum, SearchParameters parameters,
                                      boolean searchConsensus, boolean searchReference, boolean searchClusterable) {

        boolean greedy = querySpectrum.getChromatographyType() == ChromatographyType.LC_MSMS_POS
                || querySpectrum.getChromatographyType() == ChromatographyType.LC_MSMS_NEG;

        Map<BigInteger, List<BigInteger>> commonToSpectrumIdsMap = MappingUtils.toMapBigIntegerOfLists(
                spectrumRepository.preScreenSpectra(querySpectrum, parameters, greedy,
                        searchConsensus, searchReference, searchClusterable));

        List<BigInteger> preScreenedSpectrumIds =
                getSpectrumIdsWithCommonPeaksAboveThreshold(commonToSpectrumIdsMap, 50);

        Set<Long> preScreenedSpectrumIdsSet = preScreenedSpectrumIds.stream()
                .mapToLong(BigInteger::longValue)
                .boxed()
                .collect(Collectors.toSet());
        Iterable<Spectrum> preScreenedSpectra = spectrumRepository.findSpectraWithPeaksById(preScreenedSpectrumIdsSet);

        List<SpectrumMatch> matches = calculateSpectrumSimilarity(querySpectrum, preScreenedSpectra, parameters);

        return matches.subList(0, Math.min(parameters.getLimit(), matches.size()));
    }

    private List<SpectrumMatch> calculateSpectrumSimilarity(
            Spectrum querySpectrum, Iterable<Spectrum> librarySpectra, SearchParameters parameters) {

        List<SpectrumMatch> matches = new ArrayList<>();

        Double tolerance;
        boolean ppm;
        if (parameters.getMzTolerancePPM() != null) {
            tolerance = parameters.getMzTolerancePPM();
            ppm = true;
        } else {
            tolerance = parameters.getMzTolerance();
            ppm = false;
        }

        // iterate each spectrum in the adap-kdb library
        for (Spectrum librarySpectrum : librarySpectra) {

            double similarityScore = calculateCosineSimilarity(querySpectrum.getPeaks(), librarySpectrum.getPeaks(), tolerance, ppm);

            // if the similarity score > ScoreThreshold, then return the MatchSpectrum
            if (similarityScore > parameters.getScoreThreshold()) {
                SpectrumMatch matchSpectrum = new SpectrumMatch();
                matchSpectrum.setScore(similarityScore);
                matchSpectrum.setQuerySpectrum(querySpectrum);
                matchSpectrum.setMatchSpectrum(librarySpectrum);
                matches.add(matchSpectrum);
            }
        }

        matches.sort(Comparator.comparing(SpectrumMatch::getScore).reversed());
        return matches;
    }

    private List<BigInteger> getSpectrumIdsWithCommonPeaksAboveThreshold(
            Map<BigInteger, List<BigInteger>> commonToSpectrumIdsMap, long threshold) {

        List<BigInteger> spectraList = new ArrayList<>();
        for (BigInteger i = BigInteger.valueOf(8);i.compareTo(BigInteger.ZERO) > 0; i = i.subtract(BigInteger.ONE)) {
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

        double lowerFactor = 1.0 - 1E-6 * tolerance;
        double upperFactor = 1.0 + 1E-6 * tolerance;

        Iterator<Peak> queryPeakIterator = queryPeaks.iterator();
        Iterator<Peak> libraryPeakIterator = libraryPeaks.iterator();

        double dotProduct = 0.0;
        double queryNorm2 = 0.0;
        double libraryNorm2 = 0.0;
        Peak queryPeak = null;
        Peak libraryPeak = null;
        while (queryPeakIterator.hasNext() && libraryPeakIterator.hasNext()) {

            if (queryPeak == null) {
                queryPeak = queryPeakIterator.next();
                continue;
            }

            if (libraryPeak == null) {
                libraryPeak = libraryPeakIterator.next();
                continue;
            }

            double queryMz = queryPeak.getMz();
            double libraryMz = libraryPeak.getMz();
            boolean queryMzLessThanLibraryMz = ppm ? queryMz < libraryMz * lowerFactor : queryMz < libraryMz - tolerance;
            boolean queryMzGreaterThanLibraryMz = ppm ? libraryMz * upperFactor < queryMz : queryMz > libraryMz + tolerance;

            if (queryMzLessThanLibraryMz) {
                double x = scale(queryPeak);
                queryNorm2 += x * x;
                queryPeak = queryPeakIterator.next();

            } else if (queryMzGreaterThanLibraryMz) {
                double y = scale(libraryPeak);
                libraryNorm2 += y * y;
                libraryPeak = libraryPeakIterator.next();

            } else {  // queryMz and libraryMz are withing the tolerance
                double x = scale(queryPeak);
                double y = scale(libraryPeak);
                dotProduct += x * y;
                queryNorm2 += x * x;
                libraryNorm2 += y * y;
                queryPeak = queryPeakIterator.next();
                libraryPeak = libraryPeakIterator.next();
            }
        }

        return dotProduct * dotProduct / (queryNorm2 * libraryNorm2);
    }

    private double scale(Peak peak) {
        return Math.pow(peak.getIntensity(), 0.4) * Math.pow(peak.getMz(), 0.25);
    }
}
