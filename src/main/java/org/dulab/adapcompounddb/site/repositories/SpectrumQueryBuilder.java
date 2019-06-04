package org.dulab.adapcompounddb.site.repositories;

import java.util.Set;
import java.util.stream.Collectors;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;

public class SpectrumQueryBuilder {

    private final SearchType searchType;

    private final ChromatographyType chromatographyType;

    private final Set<Spectrum> excludeSpectra;

    private Range precursorRange = null;

    private Range retentionTimeRange = null;

    private Spectrum spectrum = null;

    private Set<String> tags = null;

    private double mzTolerance;

    private double scoreThreshold;


    public SpectrumQueryBuilder(SearchType searchType,
                                ChromatographyType chromatographyType,
                                Set<Spectrum> excludeSpectra) {

        this.searchType = searchType;
        this.chromatographyType = chromatographyType;
        this.excludeSpectra = excludeSpectra;
    }

    // *******************
    // ***** Setters *****
    // *******************

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

    public SpectrumQueryBuilder setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public String build() {

        // -------------------------
        // Library spectra selection
        // -------------------------

        StringBuilder librarySelectionBuilder = new StringBuilder();

        switch (searchType) {

            case CLUSTERING:
                librarySelectionBuilder.append("Consensus IS FALSE AND Reference IS FALSE");
                break;

            case SIMILARITY_SEARCH:
            default:
                librarySelectionBuilder.append("(Consensus IS TRUE OR Reference IS TRUE)");
        }

        librarySelectionBuilder.append(
                String.format(" AND ChromatographyType = \"%s\"", chromatographyType));

        if (precursorRange != null)
            librarySelectionBuilder.append(
                    String.format(" AND Precursor > %f AND Precursor < %f",
                            precursorRange.getStart(), precursorRange.getEnd()));

        if (retentionTimeRange != null)
            librarySelectionBuilder.append(
                    String.format(" AND RetentionTime > %f AND RetentionTime < %f",
                            retentionTimeRange.getStart(), retentionTimeRange.getEnd()));

        if (excludeSpectra != null)
            librarySelectionBuilder.append(
                    String.format(" AND SpectrumId NOT IN (%s)",
                            excludeSpectra.stream()
                                    .map(s -> Long.toString(s.getId()))
                                    .distinct()
                                    .collect(Collectors.joining(","))));

        if (tags != null)
            librarySelectionBuilder.append(
                    String.format(" AND (%s)",
                            tags.stream()
                                    .map(t -> String.format("SubmissionTagName = \"%s\"", t))
                                    .collect(Collectors.joining(" OR "))));

        // --------------------------------
        // End of library spectra selection
        // --------------------------------

        // -----------------------
        // Start of spectrum match
        // -----------------------

        String query;
        if (spectrum == null)
            query = "SELECT DISTINCT SpectrumId, 0 AS Score FROM Peak\n";

        else {
            query = "WITH\n";
            query += "\tCommonTable AS (SELECT SpectrumId, Mz, Intensity FROM Peak, Spectrum WHERE Peak.SpectrumId = Spectrum.Id AND ";
            query += librarySelectionBuilder.toString() + ")\n";
            query += "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n";  // 0 AS Id, NULL AS QuerySpectrumId, SpectrumId AS MatchSpectrumId

            query += spectrum.getPeaks()
                    .stream()
                    .map(p -> String.format("\tSELECT SpectrumId, MAX(SQRT(Intensity * %f)) AS Product " +
                                    "FROM CommonTable " +
                                    "WHERE Mz > %f AND Mz < %f GROUP BY SpectrumId\n",
                            p.getIntensity(),
                            p.getMz() - mzTolerance,
                            p.getMz() + mzTolerance))
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
