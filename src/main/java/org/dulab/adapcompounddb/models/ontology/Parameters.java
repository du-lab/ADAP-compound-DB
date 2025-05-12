package org.dulab.adapcompounddb.models.ontology;

public class Parameters {
    public static final int MZ_TOLERANCE_PPM = 15;
    public static final double SCORE_THRESHOLD = 0.3;
    public static final int PRECURSOR_TOLERANCE_PPM = 5;
    public static final int MASS_TOLERANCE_PPM = 5;
    public static final double RET_TIME_TOLERANCE = 0.5;
    public static final double ISOTOPIC_SIMILARITY_THRESHOLD = 0.9;

    public static String getSearchParametersAsString() {
        final String COMMA = ", ";
        return "M/Z Tolerance (PPM) = " + MZ_TOLERANCE_PPM + COMMA +
                "Precursor Tolerance (PPM) = " + PRECURSOR_TOLERANCE_PPM + COMMA +
                "Mass Tolerance (PPM) = " + MASS_TOLERANCE_PPM + COMMA +
                "Retention Time Tolerance = " + RET_TIME_TOLERANCE + COMMA +
                "Isotopic Similarity Threshold = " + ISOTOPIC_SIMILARITY_THRESHOLD + COMMA +
                "Score Threshold = " + SCORE_THRESHOLD;
    }
}
