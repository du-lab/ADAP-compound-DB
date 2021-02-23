package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.List;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;

public interface SpectrumRepositoryCustom {

    @Deprecated
    List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params);

    Iterable<SpectrumClusterView> matchAgainstConsensusAndReferenceSpectra(
            Iterable<BigInteger> submissionIds, Spectrum querySpectrum, Double scoreThreshold, Double mzTolerance,
            Double precursorTolerance, Double neutralMassTolerance, Double retTimeTolerance);

    Iterable<SpectrumMatch> matchAgainstClusterableSpectra(
            Iterable<BigInteger> submissionIds, Spectrum querySpectrum, Double scoreThreshold, Double mzTolerance,
            Double precursorTolerance, Double neutralMassTolerance, Double retTimeTolerance);

    void savePeaksAndPropertiesQuery(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

    void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList);

    void savePeaksAndProperties(Long spectrumId, List<Peak> peaks, List<SpectrumProperty> properties);
}
