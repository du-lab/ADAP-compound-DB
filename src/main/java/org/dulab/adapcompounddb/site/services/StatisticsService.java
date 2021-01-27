package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;

public interface StatisticsService {

    Statistics getStatistics(ChromatographyType type);
}
