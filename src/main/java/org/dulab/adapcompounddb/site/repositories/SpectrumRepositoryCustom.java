package org.dulab.adapcompounddb.site.repositories;

import java.util.List;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.models.entities.views.MassSearchResult;

public interface SpectrumRepositoryCustom {

    List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params);

    Iterable<SpectrumClusterView> searchLibrarySpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance,
                                                       Iterable<Long> submissionIds);

    Iterable<MassSearchResult> searchLibraryMasses(Spectrum querySpectrum, double tolerance,
                                                   String species, String source, String disease);

    void savePeaksAndPropertiesQuery(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

    void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList);

    void savePeaksAndProperties(Long spectrumId, List<Peak> peaks, List<SpectrumProperty> properties);

    List<SpectrumMatch> multiSpectrumSearch(List<Spectrum> querySpectra);
}
