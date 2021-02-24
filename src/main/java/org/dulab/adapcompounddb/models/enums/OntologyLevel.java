package org.dulab.adapcompounddb.models.enums;

import org.dulab.adapcompounddb.site.services.search.SearchParameters;

import java.util.Arrays;

public enum OntologyLevel implements EnumWithLabels {

    OL1("OL_1", 1, true, new SearchParameters()
            .setMzTolerance(Constants.MZ_TOLERANCE)
            .setScoreThreshold(Constants.SCORE_THRESHOLD)
            .setPrecursorTolerance(Constants.PRECURSOR_TOLERANCE)
            .setMassTolerance(Constants.MASS_TOLERANCE)
            .setRetTimeTolerance(Constants.RET_TIME_TOLERANCE)),
    OL2A("OL_2a", 2, true, new SearchParameters()
            .setMassTolerance(Constants.MASS_TOLERANCE)
            .setRetTimeTolerance(Constants.RET_TIME_TOLERANCE)),
    OL2B("OL_2b", 2, true, new SearchParameters()
            .setMzTolerance(Constants.MZ_TOLERANCE)
            .setScoreThreshold(Constants.SCORE_THRESHOLD)
            .setPrecursorTolerance(Constants.PRECURSOR_TOLERANCE)
            .setMassTolerance(Constants.MASS_TOLERANCE)),
    PDA("PD_A", 2, false, new SearchParameters()
            .setMzTolerance(Constants.MZ_TOLERANCE)
            .setScoreThreshold(Constants.SCORE_THRESHOLD)
            .setPrecursorTolerance(Constants.PRECURSOR_TOLERANCE)
            .setMassTolerance(Constants.MASS_TOLERANCE));


    private final String label;
    private final int priority;
    private final boolean inHouseLibrary;
    private final SearchParameters searchParameters;

    OntologyLevel(String label, int priority, boolean inHouseLibrary, SearchParameters searchParameters) {
        this.label = label;
        this.priority = priority;
        this.inHouseLibrary = inHouseLibrary;
        this.searchParameters = searchParameters;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isInHouseLibrary() {
        return inHouseLibrary;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public static int[] priorities() {
        return Arrays.stream(OntologyLevel.values())
                .mapToInt(OntologyLevel::getPriority)
                .distinct()
                .sorted()
                .toArray();
    }

    public static OntologyLevel[] findByPriority(int priority) {
        return Arrays.stream(OntologyLevel.values())
                .filter(l -> l.priority == priority)
                .toArray(OntologyLevel[]::new);
    }

    private static class Constants {
        private static final double MZ_TOLERANCE = 0.001;
        private static final double SCORE_THRESHOLD = 0.3;
        private static final double PRECURSOR_TOLERANCE = 0.01;
        private static final double MASS_TOLERANCE = 0.01;
        private static final double RET_TIME_TOLERANCE = 0.1;
    }
}
