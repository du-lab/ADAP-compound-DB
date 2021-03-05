package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Peak;

public class SpectrumQueryBuilderAlt {

    private static final String AGGREGATED_SPECTRUM_CLUSTER_VIEW_OUTPUT = "UUID_SHORT() AS UniqueId, Spectrum.Id, Spectrum.ClusterId, " +
            "Spectrum.Name, COUNT(DISTINCT File.SubmissionId) AS Size, Score, MassError, MassErrorPPM, RetTimeError, " +
            "AVG(Spectrum.Significance) AS AverageSignificance, MIN(Spectrum.Significance) AS MinimumSignificance, " +
            "MAX(Spectrum.Significance) AS MaximumSignificance, Spectrum.ChromatographyType";

    private static final String SIMPLE_SPECTRUM_CLUSTER_VIEW_OUTPUT = "UUID_SHORT() AS UniqueId, Spectrum.Id, Spectrum.ClusterId, " +
            "Spectrum.Name, 1 AS Size, Score, MassError, MassErrorPPM, RetTimeError, " +
            "Spectrum.Significance AS AverageSignificance, Spectrum.Significance AS MinimumSignificance, " +
            "Spectrum.Significance AS MaximumSignificance, Spectrum.ChromatographyType";

    private static final String EMPTY_SPECTRUM_CLUSTER_VIEW_OUTPUT = "UUID_SHORT() AS UniqueId, Spectrum.Id, Spectrum.ClusterId, " +
            "Spectrum.Name, 0 AS Size, 0 AS Score, NULL AS MassError, NULL AS MassErrorPPM, NULL AS RetTimeError, " +
            "Spectrum.Significance AS AverageSignificance, Spectrum.Significance AS MinimumSignificance, " +
            "Spectrum.Significance AS MaximumSignificance, Spectrum.ChromatographyType";

    private static final String SPECTRUM_MATCH_OUTPUT =
            "0 AS Id, NULL AS QuerySpectrumId, Spectrum.Id AS MatchSpectrumId, Score";

    private static final String EMPTY_SPECTRUM_MATCH_OUTPUT =
            "0 AS Id, NULL AS QuerySpectrumId, Spectrum.Id AS MatchSpectrumId, 0 AS Score";


    private final Collection<BigInteger> submissionIds;
    private final int limit;
    private final boolean searchConsensusSpectra;
    private final boolean searchReferenceSpectra;
    private final boolean searchClusterableSpectra;

    private ChromatographyType chromatographyType;
    private Double precursorMz = null;
    private Double precursorTolerance = null;
    private Double retTime = null;
    private Double retTimeTolerance = null;
    private Double neutralMass = null;
    private Double neutralMassTolerance = null;
    private List<Peak> peaks = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;


    public SpectrumQueryBuilderAlt(Collection<BigInteger> submissionIds, int limit,  boolean searchConsensusSpectra,
                                   boolean searchReferenceSpectra, boolean searchClusterableSpectra) {

        this.submissionIds = submissionIds;
        this.limit = limit;
        this.searchConsensusSpectra = searchConsensusSpectra;
        this.searchReferenceSpectra = searchReferenceSpectra;
        this.searchClusterableSpectra = searchClusterableSpectra;
    }

    public  SpectrumQueryBuilderAlt withChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
        return this;
    }

    public SpectrumQueryBuilderAlt withPrecursor(Double mz, Double tolerance) {
        this.precursorMz = mz;
        this.precursorTolerance = tolerance;
        return this;
    }

    public SpectrumQueryBuilderAlt withRetTime(Double retTime, Double tolerance) {
        this.retTime = retTime;
        this.retTimeTolerance = tolerance;
        return this;
    }

    public SpectrumQueryBuilderAlt withNeutralMass(Double weight, Double tolerance) {
        this.neutralMass = weight;
        this.neutralMassTolerance = tolerance;
        return this;
    }

    public SpectrumQueryBuilderAlt withQuerySpectrum(List<Peak> peaks, Double mzTolerance, Double scoreThreshold) {
        this.peaks = peaks;
        this.mzTolerance = mzTolerance;
        this.scoreThreshold = scoreThreshold;
        return this;
    }

    public String buildSpectrumClusterViewQuery() {
        return build(AGGREGATED_SPECTRUM_CLUSTER_VIEW_OUTPUT,
                SIMPLE_SPECTRUM_CLUSTER_VIEW_OUTPUT, EMPTY_SPECTRUM_CLUSTER_VIEW_OUTPUT);
    }

    public String buildSpectrumMatchQuery() {
        return build(SPECTRUM_MATCH_OUTPUT, SPECTRUM_MATCH_OUTPUT, EMPTY_SPECTRUM_MATCH_OUTPUT);
    }

    private String build(String aggregatedOutput, String simpleOutput, String emptyOutput) {

        if (submissionIds == null || submissionIds.isEmpty())
            return buildEmptyQuery(emptyOutput);

        String consensusSpectraQuery = buildConsensusSpectraQuery(aggregatedOutput);
        String referenceSpectraQuery = buildReferenceSpectraQuery(simpleOutput);
        String clusterableSpectraQuery = buildClusterableSpectraQuery(simpleOutput);

        String query = Stream.of(new String[]{consensusSpectraQuery, referenceSpectraQuery, clusterableSpectraQuery})
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\nUNION ALL\n"));
        query += String.format("\nORDER BY Score DESC, MassError ASC, RetTimeError ASC LIMIT %d", limit);

        return query;
    }

    private String buildConsensusSpectraQuery(String output) {

        if (!searchConsensusSpectra) return null;

        String query = String.format("SELECT %s FROM (\n", output);
        query += getScoreTable(true, false, false);
        query += ") AS ScoreTable JOIN SpectrumCluster ON SpectrumCluster.ConsensusSpectrumId = SpectrumId\n";
        query += "JOIN Spectrum ON Spectrum.Id = SpectrumId\n";
        query += "JOIN Spectrum AS ClusteredSpectrum ON ClusteredSpectrum.ClusterId = SpectrumCluster.Id\n";
        query += "JOIN File ON File.Id = ClusteredSpectrum.FileId\n";
        query += String.format("WHERE File.SubmissionId IN (%s)\n", submissionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        query += "GROUP BY ClusteredSpectrum.ClusterId";
        return query;
    }

    private String buildReferenceSpectraQuery(String output) {

        if (!searchReferenceSpectra) return null;

        String query = String.format("SELECT %s FROM (\n", output);
        query += getScoreTable(false, true, false);
        query += ") AS ScoreTable JOIN Spectrum ON Spectrum.Id = SpectrumId\n";
        query += "JOIN File ON File.Id = Spectrum.FileId\n";
        query += String.format("WHERE File.SubmissionId IN (%s)\n", submissionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

        return query;
    }

    private String buildClusterableSpectraQuery(String output) {

        if (!searchClusterableSpectra) return null;

        String query = String.format("SELECT %s FROM (\n", output);
        query += getScoreTable(false, false, true);
        query += ") AS ScoreTable JOIN Spectrum ON Spectrum.Id = SpectrumId\n";
        query += "JOIN File ON File.Id = Spectrum.FileId\n";
        query += String.format("WHERE File.SubmissionId IN (%s)\n", submissionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

        return query;
    }

    private String buildEmptyQuery(String output) {
        return String.format("SELECT %s FROM Spectrum JOIN File ON Spectrum.FileId = File.Id WHERE FALSE", output);
    }

    /**
     * Return the condition for selecting spectra based on a chromatography type, consensus or reference spectrum, precursor, etc.
     *
     * @param isConsensus true if spectra must be consensus spectra
     * @param isReference true if spectra must be reference spectra
     * @return SQL string with the condition
     */
    private String getScoreTable(boolean isConsensus, boolean isReference, boolean isClusterable) {

        if (peaks == null && precursorMz == null && neutralMass == null)
            throw new QueryBuilderException("No search conditions provided");

        String spectrumSelector = String.format(
                "Spectrum.Consensus IS %s AND Spectrum.Reference IS %s AND Spectrum.Clusterable IS %s",
                isConsensus, isReference, isClusterable);

        if (chromatographyType != null)
            spectrumSelector += String.format(" AND Spectrum.ChromatographyType = '%s'", chromatographyType);

        if (this.precursorMz != null && this.precursorTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    this.precursorMz - this.precursorTolerance,
                    this.precursorMz + this.precursorTolerance);

        if (this.neutralMass != null && this.neutralMassTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f",
                    this.neutralMass - this.neutralMassTolerance,
                    this.neutralMass + this.neutralMassTolerance);

        if (this.retTime != null && this.retTimeTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.RetentionTime > %f AND Spectrum.RetentionTime < %f",
                    this.retTime - this.retTimeTolerance,
                    this.retTime + this.retTimeTolerance);

        String scoreTable = "";
        if (peaks != null && mzTolerance != null && scoreThreshold != null) {
            scoreTable += String.format("SELECT SpectrumId, POWER(SUM(Product), 2) AS Score, MAX(ABS(MolecularWeight - %f)) AS MassError, ", neutralMass);
            scoreTable += String.format("1E6 * MAX(ABS(MolecularWeight - %f) / MolecularWeight) AS MassErrorPPM, ", neutralMass);
            scoreTable += String.format("MAX(ABS(RetentionTime - %f)) AS RetTimeError FROM (\n", retTime);
//            scoreTable += String.format(
//                    "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score, MAX(ABS(MolecularWeight - %f)) AS MassError, MAX(ABS(RetentionTime - %f)) AS RetTimeError FROM (\n",
//                    neutralMass, retTime);
            String finalSpectrumSelector = spectrumSelector;
            scoreTable += peaks.stream()
                    .map(p -> String.format(
                            "\tSELECT SpectrumId, SQRT(Intensity * %f) AS Product, MolecularWeight, RetentionTime " +
                                    "FROM Spectrum INNER JOIN Peak ON Peak.SpectrumId = Spectrum.Id " +
                                    "WHERE %s AND Mz > %f AND Mz < %f\n",
                            p.getIntensity(),
                            finalSpectrumSelector, p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                    .collect(Collectors.joining("\tUNION ALL\n"));
            scoreTable += ") AS SearchTable ";
            scoreTable += String.format("GROUP BY SpectrumId HAVING Score > %f\n", scoreThreshold);

        } else {
            scoreTable += String.format("\tSELECT Id AS SpectrumId, NULL AS Score, ABS(MolecularWeight - %f) AS MassError, ", neutralMass);
            scoreTable += String.format("1E6 * ABS(MolecularWeight - %f) / MolecularWeight AS MassErrorPPM, ", neutralMass);
            scoreTable += String.format("ABS(RetentionTime - %f) AS RetTimeError ", retTime);
            scoreTable += String.format("FROM Spectrum WHERE %s\n", spectrumSelector);
//            scoreTable += String.format("\tSELECT Id AS SpectrumId, NULL AS Score, ABS(MolecularWeight - %f) AS MassError, ABS(RetentionTime - %f) AS RetTimeError " +
//                            "FROM Spectrum WHERE %s\n",
//                    neutralMass, retTime, spectrumSelector);
        }
        return scoreTable;
    }
}
