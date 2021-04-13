package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

import java.util.*;

public class JavaSpectrumSimilarityService {

    public static List<SpectrumMatch> calculateSpectrumSimilarity(
            Spectrum querySpectrum, Iterable<Spectrum> librarySpectra, SearchParameters parameters) {

        Iterator<Spectrum> librarySpectrumIterator = librarySpectra.iterator();

        List<SpectrumMatch> matchSpectrumList = new ArrayList<>();

        // iterate each spectrum in the adap-kdb library
        //TODO It should work without n and sizeOfLibrarySpectra. I don't think you need these.
        int n = 0;
        int sizeOfLibrarySpectra = ((ArrayList) librarySpectra).size();
        while (librarySpectrumIterator.hasNext() && n <= sizeOfLibrarySpectra) {
            n++;
            //TODO Replace `librarySpectra.iterator()` with `librarySpectrumIterator`
            // Every time when you call `librarySpectra.iterator()`, a new iterator is created pointing to the first library spectrum.
            // So `librarySpectra.iterator().next()` gives you the same spectrum every time.
            Spectrum librarySpectrum = librarySpectra.iterator().next();
            //TODO Replace `List<Double> productList` with `double sum = 0.0`
            List<Double> productList = new ArrayList<>();
            // iterate each peak of query spectrum and current library spectrum, and calculate the product value
            for (Peak p1 : querySpectrum.getPeaks()) {
                double queryIntensity = p1.getIntensity();
                double queryMz = p1.getMz();
                for (Peak p2 : librarySpectrum.getPeaks()) {
                    double libraryIntensity = p2.getIntensity();
                    double libraryMz = p2.getMz();
                    //TODO You can move the product calculation inside the if-statement. It's a waste of time to calculate this product for unmatched peaks
                    double product = Math.sqrt(queryIntensity * libraryIntensity);
                    // if ABS(Mz - 𝑚𝑧𝑛) < MzTolerance, then add it to the product list for calculate similarity score later
                    if (Math.abs(libraryMz - queryMz) < parameters.getMzTolerance()) {
                        //TODO Replace this line with `sum += product`
                        productList.add(product);
                    }
                }
            }
            // calculate the similarity score
            //TODO Replace `productList.stream().mapToDouble(Double::doubleValue).sum()` with `sum`
            double similarityScore = Math.pow(productList.stream().mapToDouble(Double::doubleValue).sum(), 2);

            // if the similarity score > ScoreThreshold, then return the MatchSpectrum
            if (similarityScore > parameters.getScoreThreshold()) {
                SpectrumMatch matchSpectrum = new SpectrumMatch();
                matchSpectrum.setScore(similarityScore);
                matchSpectrum.setQuerySpectrum(querySpectrum);
                matchSpectrum.setMatchSpectrum(librarySpectrum);
                matchSpectrumList.add(matchSpectrum);
            }
        }
        return matchSpectrumList;
    }
}
