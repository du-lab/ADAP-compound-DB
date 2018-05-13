package org.dulab.site.services;

import org.dulab.models.ChromatographyType;
import org.dulab.models.Statistics;

public interface StatisticsService {

    Statistics getStatistics(ChromatographyType type);
}
