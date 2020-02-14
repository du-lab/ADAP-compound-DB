package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpectrumSearchServiceGCImpl implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public SpectrumSearchServiceGCImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum, parameters);
    }

    @Override
    @Transactional
    public List<ClusterDTO> searchConsensusSpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance) {
        List<ClusterDTO> clusters = new ArrayList<>();
        for (SpectrumClusterView view :
                spectrumRepository.searchConsensusSpectra(querySpectrum, scoreThreshold, mzTolerance)) {

            ClusterDTO cluster = new ClusterDTO();
            cluster.setClusterId(view.getId());
            cluster.setConsensusSpectrumName(view.getName());
            cluster.setSize(view.getSize());
            cluster.setScore(view.getScore());
            cluster.setAveSignificance(view.getAverageSignificance());
            cluster.setMinSignificance(view.getMinimumSignificance());
            cluster.setMaxSignificance(view.getMaximumSignificance());
            cluster.setChromatographyTypeLabel(view.getChromatographyType().getLabel());
            cluster.setChromatographyTypePath(view.getChromatographyType().getIconPath());
            clusters.add(cluster);
        }
        return clusters;
    }
}
