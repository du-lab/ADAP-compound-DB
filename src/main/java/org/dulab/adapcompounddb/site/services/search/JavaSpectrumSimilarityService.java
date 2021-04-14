package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

import java.util.*;
import java.util.stream.Collectors;

public class JavaSpectrumSimilarityService {

    public static List<SpectrumMatch> calculateSpectrumSimilarity(
            Spectrum querySpectrum, Iterable<Spectrum> librarySpectra, SearchParameters parameters) {

        Iterator<Spectrum> librarySpectrumIterator = librarySpectra.iterator();

        List<SpectrumMatch> matchSpectrumList = new ArrayList<>();

        // iterate each spectrum in the adap-kdb library
        while (librarySpectrumIterator.hasNext() ) {
            Spectrum librarySpectrum = librarySpectrumIterator.next();

            double sum = 0.0;
            // iterate each peak of query spectrum and current library spectrum, and calculate the product value
            for (Peak p1 : querySpectrum.getPeaks()) {
                double queryIntensity = p1.getIntensity();
                double queryMz = p1.getMz();
                for (Peak p2 : librarySpectrum.getPeaks()) {
                    double libraryIntensity = p2.getIntensity();
                    double libraryMz = p2.getMz();

                    // if ABS(Mz - 𝑚𝑧𝑛) < MzTolerance, then add it to the product list for calculate similarity score later
                    if (Math.abs(libraryMz - queryMz) < parameters.getMzTolerance()) {
                        double product = Math.sqrt(queryIntensity * libraryIntensity);
                        sum += product;
                    }
                }
            }
            // calculate the similarity score
            double similarityScore = Math.pow(sum, 2);

            // if the similarity score > ScoreThreshold, then return the MatchSpectrum
            if (similarityScore > parameters.getScoreThreshold()) {
                SpectrumMatch matchSpectrum = new SpectrumMatch();
                matchSpectrum.setScore(similarityScore);
                matchSpectrum.setQuerySpectrum(querySpectrum);
                matchSpectrum.setMatchSpectrum(librarySpectrum);
                matchSpectrumList.add(matchSpectrum);
            }
        }

        //TODO Sort matchSpectrumList by Score
        return matchSpectrumList.stream()
                .sorted(Comparator.comparing(SpectrumMatch::getScore))
                .collect(Collectors.toList());
    }
}
