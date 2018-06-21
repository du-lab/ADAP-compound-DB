package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.Set;
import java.util.stream.Collectors;

public class SpectrumQueryBuilder {

    private final String peakView;

    private final ChromatographyType chromatographyType;

    private final Set<Spectrum> excludeSpectra;

    private Range precursorRange = null;

    private Range retentionTimeRange = null;

    private Spectrum spectrum = null;

    private double mzTolerance;

    private double scoreThreshold;


    public SpectrumQueryBuilder(SearchType searchType, ChromatographyType chromatographyType, Set<Spectrum> excludeSpectra) {

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

    public SpectrumQueryBuilder setPrecursorRange(double precursor, double tolerance) {
        this.precursorRange = new Range(precursor - tolerance, precursor + tolerance);
        return this;
    }

    public SpectrumQueryBuilder setRetentionTimeRange(double retentionTime, double tolerance) {
        this.retentionTimeRange = new Range(retentionTime - tolerance, retentionTime + tolerance);
        return this;
    }

    public SpectrumQueryBuilder setSpectrum(Spectrum spectrum, double mzTolerance, double scoreThreshold) {
        this.spectrum = spectrum;
        this.mzTolerance = mzTolerance;
        this.scoreThreshold = scoreThreshold;
        return this;
    }


    public String build() {

        // -----------------------
        // Start of WITH statement
        // -----------------------

        String query = String.format("WITH PeakCTE AS (\n" +
                "\tSELECT * FROM %s\n" +
                "\tWHERE ChromatographyType = \"%s\"\n", peakView, chromatographyType);

        if (precursorRange != null)
            query += String.format("\tAND Precursor > %f AND Precursor < %f\n",
                    precursorRange.getStart(), precursorRange.getEnd());

        if (retentionTimeRange != null)
            query += String.format("\tAND RetentionTime > %f AND RetentionTime < %f\n",
                    retentionTimeRange.getStart(), retentionTimeRange.getEnd());

        if (excludeSpectra != null)
            query += String.format(
                    "\tAND SpectrumId NOT IN (%s)\n",
                    excludeSpectra.stream()
                            .map(s -> Long.toString(s.getId()))
                            .distinct()
                            .collect(Collectors.joining(", ")));


        query += ")\n";

        // ---------------------
        // End of WITH statement
        // ---------------------

        // -----------------------
        // Start of spectrum match
        // -----------------------

        if (spectrum == null)
            query += "SELECT DISTINCT SpectrumId, 0 AS Score FROM PeakCTE\n";

        else {
            query += "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n";  // 0 AS Id, NULL AS QuerySpectrumId, SpectrumId AS MatchSpectrumId

            query += spectrum.getPeaks()
                    .stream()
                    .map(p -> String.format("\tSELECT SpectrumId, MAX(SQRT(Intensity * %f)) AS Product FROM PeakCTE " +
                                    "WHERE Mz > %f AND Mz < %f GROUP BY SpectrumId\n",
                            p.getIntensity(), p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                    .collect(Collectors.joining("\tUNION ALL\n"));

            query += ") AS Result\n";
            query += String.format("GROUP BY SpectrumId HAVING Score > %f ORDER BY Score DESC\n", scoreThreshold);
        }

        // ---------------------
        // End of spectrum match
        // ---------------------

        return query;
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
