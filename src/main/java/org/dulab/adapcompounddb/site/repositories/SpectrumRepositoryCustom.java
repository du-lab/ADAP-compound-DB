package org.dulab.adapcompounddb.site.repositories;

import java.util.List;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.services.SubmissionServiceImpl;

public interface SpectrumRepositoryCustom {

    List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params);

//    void savePeaksFromSpectrum(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

    void savePeaksAndPropertiesQuery(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

//    void savePropertiesFromSpectrum(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList);
}
