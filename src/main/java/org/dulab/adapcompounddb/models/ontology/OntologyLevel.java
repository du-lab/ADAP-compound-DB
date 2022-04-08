package org.dulab.adapcompounddb.models.ontology;


public class OntologyLevel {

    private final String label;
    private final int priority;
    private final boolean inHouseLibrary;
    private final Integer mzTolerancePPM;
    private final Double scoreThreshold;
    private final Integer precursorTolerancePPM;
    private final Integer massTolerancePPM;
    private final Double retTimeTolerance;
    private final Double isotopicSimilarityThreshold;


    public OntologyLevel(String label, int priority, boolean inHouseLibrary, Integer mzTolerancePPM, Double scoreThreshold,
                         Integer precursorTolerancePPM, Integer massTolerancePPM, Double retTimeTolerance,
                         Double isotopicSimilarityThreshold) {
        this.label = label;
        this.priority = priority;
        this.inHouseLibrary = inHouseLibrary;
        this.mzTolerancePPM = mzTolerancePPM;
        this.scoreThreshold = scoreThreshold;
        this.massTolerancePPM = massTolerancePPM;
        this.precursorTolerancePPM = precursorTolerancePPM;
        this.retTimeTolerance = retTimeTolerance;
        this.isotopicSimilarityThreshold = isotopicSimilarityThreshold;
    }

    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isInHouseLibrary() {
        return inHouseLibrary;
    }

    public Integer getMzTolerancePPM() {
        return mzTolerancePPM;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public Integer getMassTolerancePPM() {
        return massTolerancePPM;
    }

    public Integer getPrecursorTolerancePPM() {
        return precursorTolerancePPM;
    }

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public Double getIsotopicSimilarityThreshold() {
        return isotopicSimilarityThreshold;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
