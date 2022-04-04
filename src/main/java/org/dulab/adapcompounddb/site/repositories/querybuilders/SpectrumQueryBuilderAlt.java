package org.dulab.adapcompounddb.site.repositories.querybuilders;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.site.repositories.QueryBuilderException;

@Deprecated
public class SpectrumQueryBuilderAlt {

    private static final String AGGREGATED_SPECTRUM_CLUSTER_VIEW_OUTPUT = "UUID_SHORT() AS UniqueId, Spectrum.Id, Spectrum.ClusterId, " +
            "Spectrum.Name, COUNT(DISTINCT File.SubmissionId) AS Size, Score, MassError, MassErrorPPM, RetTimeError, " +
            "AVG(ClusteredSpectrum.Significance) AS AverageSignificance, MIN(ClusteredSpectrum.Significance) AS MinimumSignificance, " +
            "MAX(ClusteredSpectrum.Significance) AS MaximumSignificance, Spectrum.ChromatographyType";

    private static final String SIMPLE_SPECTRUM_CLUSTER_VIEW_OUTPUT = "UUID_SHORT() AS UniqueId, Spectrum.Id, Spectrum.ClusterId, " +
            "Spectrum.Name, 1 AS Size, Score, MassError, MassErrorPPM, RetTimeError, " +
            "Spectrum.Significance AS AverageSignificance, Spectrum.Significance AS MinimumSignificance, " +
            "Spectrum.Significance AS MaximumSignificance, Spectrum.ChromatographyType";

    private static final String EMPTY_SPECTRUM_CLUSTER_VIEW_OUTPUT = "UUID_SHORT() AS UniqueId, Spectrum.Id, Spectrum.ClusterId, " +
            "Spectrum.Name, 0 AS Size, 0 AS Score, NULL AS MassError, NULL AS MassErrorPPM, NULL AS RetTimeError, " +
            "Spectrum.Significance AS AverageSignificance, Spectrum.Significance AS MinimumSignificance, " +
            "Spectrum.Significance AS MaximumSignificance, Spectrum.ChromatographyType";

    private static final String SPECTRUM_MATCH_OUTPUT =
            "UUID_SHORT() AS Id, NULL AS QuerySpectrumId, Spectrum.Id AS MatchSpectrumId, Score";

    private static final String EMPTY_SPECTRUM_MATCH_OUTPUT =
            "0 AS Id, NULL AS QuerySpectrumId, Spectrum.Id AS MatchSpectrumId, 0 AS Score";

    private final Collection<BigInteger> spectrumIds;
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
    private Double mass = null;
    private double[] masses = null;
    private Double massTolerance = null;
    private Double massTolerancePPM = null;
    private List<Peak> peaks = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;


    public SpectrumQueryBuilderAlt(Collection<BigInteger> spectrumIds, Collection<BigInteger> submissionIds,
                                   int limit, boolean searchConsensusSpectra, boolean searchReferenceSpectra,
                                   boolean searchClusterableSpectra) {

        this.spectrumIds = spectrumIds;
        this.submissionIds = submissionIds;
        this.limit = limit;
        this.searchConsensusSpectra = searchConsensusSpectra;
        this.searchReferenceSpectra = searchReferenceSpectra;
        this.searchClusterableSpectra = searchClusterableSpectra;
    }

    public SpectrumQueryBuilderAlt withChromatographyType(ChromatographyType chromatographyType) {
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

    public SpectrumQueryBuilderAlt withMass(Double mass, Double tolerance) {
        this.mass = mass;
        this.massTolerance = tolerance;
        return this;
    }

    public SpectrumQueryBuilderAlt withMassPPM(Double mass, Double ppm) {
        this.mass = mass;
        this.massTolerancePPM = ppm;
        return this;
    }

    public SpectrumQueryBuilderAlt withMasses(double[] masses, Double tolerance) {
        this.masses = masses;
        this.massTolerance = tolerance;
        return this;
    }

    public SpectrumQueryBuilderAlt withMassesPPM(double[] masses, Double ppm) {
        this.masses = masses;
        this.massTolerancePPM = ppm;
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

        if (peaks == null && precursorMz == null && mass == null)
            throw new QueryBuilderException("No search conditions provided");

        String spectrumSelector = String.format(
                "Spectrum.Consensus IS %s AND Spectrum.Reference IS %s AND Spectrum.Clusterable IS %s",
                isConsensus, isReference, isClusterable);

        if(spectrumIds != null && !spectrumIds.isEmpty())
            spectrumSelector += String.format(" And Spectrum.Id in (%s)", spectrumIds.stream().map(BigInteger::toString)
                    .collect(Collectors.joining(",")));

        if (chromatographyType != null)
            spectrumSelector += String.format(" AND Spectrum.ChromatographyType = '%s'", chromatographyType);

        if (precursorMz != null && precursorTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    precursorMz - precursorTolerance, precursorMz + precursorTolerance);

        if (mass != null && massTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.Mass > %f AND Spectrum.Mass < %f",
                    mass - massTolerance, mass + massTolerance);

        if (mass != null && massTolerancePPM != null) {
            double x = 1e-6 * massTolerancePPM;
            spectrumSelector += String.format(" AND Spectrum.Mass > %f AND Spectrum.Mass < %f",
                    mass / (1 + x), mass / (1 - x));
        }

        if (mass == null && masses != null && massTolerance != null)
            spectrumSelector += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.Mass > %f AND Spectrum.Mass < %f)",
                            mass - massTolerance, mass + massTolerance))
                    .collect(Collectors.joining(" OR ")));

        if (mass == null && masses != null && massTolerancePPM != null) {
            double x = 1e-6 * massTolerancePPM;
            spectrumSelector += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.Mass > %f AND Spectrum.Mass < %f)",
                            mass / (1 + x), mass / (1 - x)))
                    .collect(Collectors.joining(" OR ")));
        }

        if (retTime != null && retTimeTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.RetentionTime > %f AND Spectrum.RetentionTime < %f",
                    retTime - retTimeTolerance, retTime + retTimeTolerance);

        String scoreTable = "";
        if (peaks != null && mzTolerance != null && scoreThreshold != null) {
            double omega = 1.0 / (peaks.stream().mapToDouble(Peak::getIntensity).sum() - 0.5);
            double totalIntensity = peaks.stream()
                    .mapToDouble(p -> scale(p, omega))
                    .sum();
            scoreTable += String.format("SELECT SpectrumId, POWER(SUM(Product), 2) / (MAX(TotalIntensity) * %f) AS Score, ", totalIntensity);
            scoreTable += String.format("MAX(%s) AS MassError, ", getMassError());
            scoreTable += String.format("MAX(%s) AS MassErrorPPM, ", getMassErrorPPM());
            scoreTable += String.format("MAX(ABS(RetentionTime - %f)) AS RetTimeError FROM (\n", retTime);
            String finalSpectrumSelector = spectrumSelector;
            scoreTable += peaks.stream()
                    .map(p -> String.format(
                            "\tSELECT SpectrumId, TotalIntensity, SQRT(Intensity * Mz / (1 + OmegaFactor * Intensity) * %f) AS Product, MolecularWeight, RetentionTime " +
                                    "FROM Spectrum INNER JOIN Peak ON Peak.SpectrumId = Spectrum.Id " +
                                    "WHERE %s AND Mz > %f AND Mz < %f\n",
                            scale(p, omega),
                            finalSpectrumSelector, p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                    .collect(Collectors.joining("\tUNION ALL\n"));
            scoreTable += ") AS SearchTable ";
            scoreTable += String.format("GROUP BY SpectrumId HAVING Score > %f\n", scoreThreshold);

        } else {
            scoreTable += "\tSELECT Id AS SpectrumId, NULL AS Score, ";
            scoreTable += String.format("%s AS MassError, ", getMassError());
            scoreTable += String.format("%s AS MassErrorPPM, ", getMassErrorPPM());
            scoreTable += String.format("ABS(RetentionTime - %f) AS RetTimeError ", retTime);
            scoreTable += String.format("FROM Spectrum WHERE %s\n", spectrumSelector);
        }
        return scoreTable;
    }

    private String getMassError() {
        if (mass != null) {
            return String.format("ABS(Mass - %f)", mass);
        } else if (masses != null) {
            return String.format("LEAST(%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format("ABS(Mass - %f)", mass))
                    .collect(Collectors.joining(",")));
        }
        return "NULL";
    }

    private String getMassErrorPPM() {
        if (mass != null) {
            return String.format("1E6 * ABS(Mass - %f) / Mass", mass);
        } else if (masses != null) {
            return String.format("LEAST(%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format("1E6 * ABS(Mass - %f) / Mass", mass))
                    .collect(Collectors.joining(",")));
        }
        return "NULL";
    }

    private double scale(Peak peak, double omega) {
        return peak.getIntensity() * peak.getMz() / (1.0 + omega * peak.getIntensity());
    }

}
