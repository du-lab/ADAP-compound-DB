package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;

public interface SpectrumRepositoryCustom {

    @Deprecated
    List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params);

    Iterable<SpectrumClusterView> matchAgainstConsensusAndReferenceSpectra(
            List<BigInteger> spectrumIds, Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
            SearchParameters parameters);

    Iterable<SpectrumMatch> matchAgainstClusterableSpectra(
            List<BigInteger> preScreenedSpectrumIds, Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
            SearchParameters parameters);

    void savePeaksAndPropertiesQuery(List<Spectrum> spectrumList, List<Long> savedSpectrumIdList);

    void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList);

    void savePeaksAndProperties(Long spectrumId, List<Peak> peaks, List<SpectrumProperty> properties);

    Iterable<Object[]> preScreenSpectra(Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user,
                                        boolean greedy, boolean searchConsensus, boolean searchReference,
                                        boolean searchClusterable);

    Iterable<Object[]> filterSpectra(Map<BigInteger, List<BigInteger>> countToSpectrumIdMap, SearchParameters params);
}
