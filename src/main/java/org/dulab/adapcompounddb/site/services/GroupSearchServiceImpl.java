package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GroupSearchServiceImpl implements GroupSearchService {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchServiceImpl.class);

    private float progress = 0;
    private final SpectrumRepository spectrumRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public GroupSearchServiceImpl(SpectrumRepository spectrumRepository, SubmissionRepository submissionRepository) {
        this.spectrumRepository = spectrumRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Void> groupSearch(
            List<Spectrum> querySpectra, HttpSession session, String species, String source, String disease) {

        LOGGER.info(String.format("Group search is started (species: %s, source: %s, disease: %s)",
                species != null ? species : "all",
                source != null ? source : "all",
                disease != null ? disease : "all"));

        try {
            final List<ClusterDTO> groupSearchDTOList = new ArrayList<>();
            session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);

            // Calculate total number of spectra
            long totalSteps = querySpectra.size();

            if (totalSteps == 0) {
                LOGGER.warn("No query spectra for performing a group search");
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                return new AsyncResult<>(null);
            }

            int progressStep = 0;
            progress = 0F;
            for (Spectrum querySpectrum : querySpectra) {

                if (Thread.currentThread().isInterrupted()) break;

                ClusterDTO clusterDTO = new ClusterDTO();
                clusterDTO.setQuerySpectrumName(querySpectrum.getName());
                clusterDTO.setQuerySpectrumId(querySpectrum.getId());

                List<SpectrumClusterView> clusters = MappingUtils.toList(
                        spectrumRepository.searchConsensusSpectra(
                                querySpectrum, 0.25, 0.01, species, source, disease));

                // get the best match if the match is not null
                if (clusters.size() > 0) {
                    SpectrumClusterView clusterView = clusters.get(0);
                    clusterDTO.setClusterId(clusterView.getId());
                    clusterDTO.setConsensusSpectrumName(clusterView.getName());
                    clusterDTO.setSize(clusterView.getSize());
                    clusterDTO.setScore(clusterView.getScore());
                    clusterDTO.setAveSignificance(clusterView.getAverageSignificance());
                    clusterDTO.setMinSignificance(clusterView.getMinimumSignificance());
                    clusterDTO.setMaxSignificance(clusterView.getMaximumSignificance());
                    clusterDTO.setChromatographyTypeLabel(clusterView.getChromatographyType().getLabel());
                    clusterDTO.setChromatographyTypePath(clusterView.getChromatographyType().getIconPath());
                }

                if (Thread.currentThread().isInterrupted()) break;

                groupSearchDTOList.add(clusterDTO);
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                progress = (float) ++progressStep / totalSteps;
            }
        } catch (Throwable t) {
            LOGGER.error(String.format("Error during the group search (species: %s, source: %s, disease: %s): %s",
                    species != null ? species : "all",
                    source != null ? source : "all",
                    disease != null ? disease : "all",
                    t.getMessage()), t);
            throw t;
        }

        if (Thread.currentThread().isInterrupted())
            LOGGER.info(String.format("Group search is cancelled (species: %s, source: %s, disease: %s)",
                    species != null ? species : "all",
                    source != null ? source : "all",
                    disease != null ? disease : "all"));

        return new AsyncResult<>(null);
    }
}
