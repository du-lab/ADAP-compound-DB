package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;

public interface StatisticsService {

    Statistics getStatistics(ChromatographyType type);
}
