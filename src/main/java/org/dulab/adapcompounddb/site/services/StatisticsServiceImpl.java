package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;

    public StatisticsServiceImpl(SpectrumRepository spectrumRepository,
                                 SpectrumMatchRepository spectrumMatchRepository) {
        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
    }

    @Override
    @Transactional
    public Statistics getStatistics(ChromatographyType type) {
        Statistics statistics = new Statistics();
        statistics.setNumSubmittedSpectra(
                spectrumRepository.countBySubmissionChromatographyTypeAndConsensusFalse(type));
        statistics.setNumConsensusSpectra(
                spectrumRepository.countByClusterChromatographyTypeAndConsensusTrue(type));
        statistics.setNumUnmatchedSpectra(
                spectrumRepository.countUnmatchedBySubmissionChromatographyType(type));
        statistics.setNumSpectrumMatches(
                spectrumMatchRepository.countByQuerySpectrumSubmissionChromatographyType(type));
        return statistics;
    }
}
