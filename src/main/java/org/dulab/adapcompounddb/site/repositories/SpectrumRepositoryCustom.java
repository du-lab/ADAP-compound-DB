package org.dulab.adapcompounddb.site.repositories;

import java.util.List;
import java.util.Set;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;

public interface SpectrumRepositoryCustom {

    List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params);

    void savePeaksAndPropertiesQuery(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

    void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList);

    void savePeaksAndProperties(Long spectrumId, List<Peak> peaks, List<SpectrumProperty> properties);

    void updateSpectraInCluster(final long clusterId, final Set<Long> spectrumIds);
}
