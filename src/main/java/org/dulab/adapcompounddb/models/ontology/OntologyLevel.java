package org.dulab.adapcompounddb.models.ontology;


public class OntologyLevel {

    private final String label;
    private final int priority;
    private final boolean inHouseLibrary;
    private final Double mzTolerancePPM;
    private final Double scoreThreshold;
    private final Double precursorTolerancePPM;
    private final Double massTolerancePPM;
    private final Double retTimeTolerance;


    public OntologyLevel(String label, int priority, boolean inHouseLibrary, Double mzTolerancePPM, Double scoreThreshold,
                         Double precursorTolerancePPM, Double massTolerancePPM, Double retTimeTolerance) {
        this.label = label;
        this.priority = priority;
        this.inHouseLibrary = inHouseLibrary;
        this.mzTolerancePPM = mzTolerancePPM;
        this.scoreThreshold = scoreThreshold;
        this.massTolerancePPM = massTolerancePPM;
        this.precursorTolerancePPM = precursorTolerancePPM;
        this.retTimeTolerance = retTimeTolerance;
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

    public Double getMzTolerancePPM() {
        return mzTolerancePPM;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public Double getMassTolerancePPM() {
        return massTolerancePPM;
    }

    public Double getPrecursorTolerancePPM() {
        return precursorTolerancePPM;
    }

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
