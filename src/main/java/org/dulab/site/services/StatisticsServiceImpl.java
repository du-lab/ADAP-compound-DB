package org.dulab.site.services;

import org.dulab.models.ChromatographyType;
import org.dulab.models.Statistics;
import org.dulab.site.repositories.SpectrumMatchRepository;
import org.dulab.site.repositories.SpectrumRepository;
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
