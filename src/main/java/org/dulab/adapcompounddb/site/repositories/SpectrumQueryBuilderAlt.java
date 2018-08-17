package org.dulab.adapcompounddb.site.repositories;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;

public class SpectrumQueryBuilderAlt {

    private final String peakView;

    private final ChromatographyType chromatographyType;

    private final Set<Spectrum> excludeSpectra;

    private Range precursorRange = null;

    private Range retentionTimeRange = null;

    private Spectrum spectrum = null;

    private double mzTolerance;

    private double scoreThreshold;


    public SpectrumQueryBuilderAlt(SearchType searchType, ChromatographyType chromatographyType, Set<Spectrum> excludeSpectra) {

        switch (searchType) {
            case SIMILARITY_SEARCH:
                this.peakView = "SearchSpectrumPeakView";
                break;
            case CLUSTERING:
                this.peakView = "ClusterSpectrumPeakView";
                break;
            default:
                this.peakView = "SearchSpectrumPeakView";
        }

        this.chromatographyType = chromatographyType;
        this.excludeSpectra = excludeSpectra;
    }

    public SpectrumQueryBuilderAlt setPrecursorRange(double precursor, double tolerance) {
        this.precursorRange = new Range(precursor - tolerance, precursor + tolerance);
        return this;
    }

    public SpectrumQueryBuilderAlt setRetentionTimeRange(double retentionTime, double tolerance) {
        this.retentionTimeRange = new Range(retentionTime - tolerance, retentionTime + tolerance);
        return this;
    }

    public SpectrumQueryBuilderAlt setSpectrum(Spectrum spectrum, double mzTolerance, double scoreThreshold) {
        this.spectrum = spectrum;
        this.mzTolerance = mzTolerance;
        this.scoreThreshold = scoreThreshold;
        return this;
    }


    public String[] build() {

        String sqlQuery = "CREATE TEMPORARY TABLE Condition (Intensity DOUBLE NOT NULL, MzMin DOUBLE NOT NULL, MzMax DOUBLE NUT NULL, CONSTRAINT Condition_pk PRIMARY KEY (MzMin, MzMax));\n";
        sqlQuery += "INSERT INTO Condition (Intensity, MzMin, MzMax) VALUES\n";
        sqlQuery += spectrum.getPeaks()
                .stream()
                .map(p -> String.format("(%f,%f,%f)", p.getIntensity(), p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                .collect(Collectors.joining(",")) + ";\n";

        sqlQuery += "SELECT SpectrumId, POWER(SUM(SearchSpectrumPeakView.Intensity * Condition.Intensity), 2) AS Score FROM SearchSpectrumPeakView, Condition\n" +
                "WHERE ChromatographyType = \"GAS\" AND Mz < MzMin AND Mz > MzMax" +
                "GROUP BY SpectrumId HAVING Score > %f ORDER BY Score DESC";

        final String commonTableName = "PeakCTE" + RandomStringUtils.randomAlphanumeric(12);

        // -----------------------
        // Start of WITH statement
        // -----------------------

//        String query = String.format("WITH PeakCTE AS (\n" +
        String initQuery = String.format("CREATE TABLE %s AS (\n" +  // ENGINE=MEMORY
                "\tSELECT * FROM %s\n" +
                "\tWHERE ChromatographyType = \"%s\"\n", commonTableName, peakView, chromatographyType);

        if (precursorRange != null)
            initQuery += String.format("\tAND Precursor > %f AND Precursor < %f\n",
                    precursorRange.getStart(), precursorRange.getEnd());

        if (retentionTimeRange != null)
            initQuery += String.format("\tAND RetentionTime > %f AND RetentionTime < %f\n",
                    retentionTimeRange.getStart(), retentionTimeRange.getEnd());

        if (excludeSpectra != null)
            initQuery += String.format(
                    "\tAND SpectrumId NOT IN (%s)\n",
                    excludeSpectra.stream()
                            .map(s -> Long.toString(s.getId()))
                            .distinct()
                            .collect(Collectors.joining(", ")));


        initQuery += ");\n";

        // ---------------------
        // End of WITH statement
        // ---------------------

        // -----------------------
        // Start of spectrum match
        // -----------------------


        String selectQuery;
        if (spectrum == null)
            selectQuery = String.format("SELECT DISTINCT SpectrumId, 0 AS Score FROM %s\n", peakView);

        else {
            selectQuery = "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n";  // 0 AS Id, NULL AS QuerySpectrumId, SpectrumId AS MatchSpectrumId

            selectQuery += spectrum.getPeaks()
                    .stream()
                    .map(p -> String.format("\tSELECT SpectrumId, MAX(SQRT(Intensity * %f)) AS Product FROM %s " +
                                    "WHERE Mz > %f AND Mz < %f GROUP BY SpectrumId\n",
                            p.getIntensity(), commonTableName, p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                    .collect(Collectors.joining("\tUNION ALL\n"));

            selectQuery += ") AS Result\n";
            selectQuery += String.format("GROUP BY SpectrumId HAVING Score > %f ORDER BY Score DESC\n", scoreThreshold);
        }

        // ---------------------
        // End of spectrum match
        // ---------------------

        String dropQuery = String.format("DROP TABLE %s;", commonTableName);

        return new String[] {initQuery, selectQuery, dropQuery};
    }


    public static class Range {

        private final double start;
        private final double end;

        public Range(double start, double end) {
            this.start = start;
            this.end = end;
        }

        public double getStart() {
            return start;
        }

        public double getEnd() {
            return end;
        }
    }
}
