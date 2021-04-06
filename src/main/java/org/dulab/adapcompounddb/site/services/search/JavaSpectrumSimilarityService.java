package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

import java.util.List;

public class JavaSpectrumSimilarityService {

    //TODO Write a code to calculate similarity scores between the query spectrum and library spectra and return a list of matches
    List<SpectrumMatch> calculateSpectrumSimilarity(
            Spectrum querySpectrum, Iterable<Spectrum> librarySpectra, SearchParameters parameters) {

        return null;
    }
}
