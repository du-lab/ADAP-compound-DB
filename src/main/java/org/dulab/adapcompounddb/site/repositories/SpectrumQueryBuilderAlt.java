package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Peak;

public class SpectrumQueryBuilderAlt {

    private final Collection<BigInteger> submissionIds;
    private final boolean searchConsensusSpectra;
    private final boolean searchReferenceSpectra;

    private ChromatographyType chromatographyType;
    private Double precursorMz = null;
    private Double precursorTolerance = null;
    private Double retTime = null;
    private Double retTimeTolerance = null;
    private Double molecularWeight = null;
    private Double molecularWeightTolerance = null;
    private List<Peak> peaks = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;


    public SpectrumQueryBuilderAlt(Collection<BigInteger> submissionIds,
                                   boolean searchConsensusSpectra, boolean searchReferenceSpectra) {

        if (!searchConsensusSpectra && !searchReferenceSpectra)
            throw new IllegalArgumentException("Either 'searchConsensusSpectra' or 'searchReferenceSpectra' must be true");

        this.submissionIds = submissionIds;
        this.searchConsensusSpectra = searchConsensusSpectra;
        this.searchReferenceSpectra = searchReferenceSpectra;
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

    public SpectrumQueryBuilderAlt withMolecularWeight(Double weight, Double tolerance) {
        this.molecularWeight = weight;
        this.molecularWeightTolerance = tolerance;
        return this;
    }

    public SpectrumQueryBuilderAlt withQuerySpectrum(List<Peak> peaks, Double mzTolerance, Double scoreThreshold) {
        this.peaks = peaks;
        this.mzTolerance = mzTolerance;
        this.scoreThreshold = scoreThreshold;
        return this;
    }

    public String build() {

        if (submissionIds == null || submissionIds.isEmpty())
            return buildEmptyQuery();

        String consensusSpectraQuery = buildConsensusSpectraQuery();
        String referenceSpectraQuery = buildReferenceSpectraQuery();

        String query = null;
        if (consensusSpectraQuery != null && referenceSpectraQuery != null)
            query = String.format("%s\nUNION ALL\n%s\nORDER BY Score DESC",
                    consensusSpectraQuery, referenceSpectraQuery);
        else if (consensusSpectraQuery != null)
            query = consensusSpectraQuery + "\nORDER BY Score DESC";
        else if (referenceSpectraQuery != null)
            query = referenceSpectraQuery + "\nORDER BY Score DESC";

        return query;
    }

    private String buildConsensusSpectraQuery() {

        if (!searchConsensusSpectra) return null;

        String query = "SELECT ConsensusSpectrum.Id, SpectrumCluster.Id AS ClusterId, ConsensusSpectrum.Name, ";
        query += "COUNT(DISTINCT File.SubmissionId) AS Size, Score, Error, ";
        query += "AVG(Spectrum.Significance) AS AverageSignificance, MIN(Spectrum.Significance) AS MinimumSignificance, ";
        query += "MAX(Spectrum.Significance) AS MaximumSignificance, ConsensusSpectrum.ChromatographyType FROM (\n";
        query += getScoreTable(true, false);
        query += ") AS ScoreTable JOIN SpectrumCluster ON SpectrumCluster.ConsensusSpectrumId = SpectrumId\n";
        query += "JOIN Spectrum AS ConsensusSpectrum ON ConsensusSpectrum.Id = SpectrumId\n";
        query += "JOIN Spectrum ON Spectrum.ClusterId = SpectrumCluster.Id\n";
        query += "JOIN File ON File.Id = Spectrum.FileId\n";
        query += String.format("WHERE File.SubmissionId IN (%s)\n", submissionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        query += "GROUP BY Spectrum.ClusterId";
        return query;
    }

    private String buildReferenceSpectraQuery() {

        if (!searchReferenceSpectra) return null;

        String query = "SELECT Spectrum.Id, NULL AS ClusterId, Spectrum.Name, 1 AS Size, Score, Error, ";
        query += "Spectrum.Significance AS AverageSignificance, Spectrum.Significance AS MinimumSignificance, ";
        query += "Spectrum.Significance AS MaximumSignificance, Spectrum.ChromatographyType FROM (\n";
        query += getScoreTable(false, true);
        query += ") AS ScoreTable JOIN Spectrum ON Spectrum.Id = SpectrumId\n";
        query += "JOIN File ON File.Id = Spectrum.FileId\n";
        query += String.format("WHERE File.SubmissionId IN (%s)\n", submissionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

        return query;
    }

    private String buildEmptyQuery() {
        return "SELECT Id, NULL AS ClusterId, Name, 1 AS Size, 0 AS Score, NULL AS Error, " +
                "Significance AS AverageSignificance, Significance AS MinimumSignificance, " +
                "Significance AS MaximumSignificance, ChromatographyType FROM Spectrum WHERE FALSE";
    }

    /**
     * Return the condition for selecting spectra based on a chromatography type, consensus or reference spectrum, precursor, etc.
     *
     * @param isConsensus true if spectra must be consensus spectra
     * @param isReference true if spectra must be reference spectra
     * @return SQL string with the condition
     */
    private String getScoreTable(boolean isConsensus, boolean isReference) {

        String spectrumSelector = String.format(
                "Spectrum.Consensus IS %s AND Spectrum.Reference IS %s", isConsensus, isReference);

        if (chromatographyType != null)
            spectrumSelector += String.format(" AND Spectrum.ChromatographyType = '%s'", chromatographyType);

        if (this.precursorMz != null && this.precursorTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    this.precursorMz - this.precursorTolerance,
                    this.precursorMz + this.precursorTolerance);

        if (this.molecularWeight != null && this.molecularWeightTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f",
                    this.molecularWeight - this.molecularWeightTolerance,
                    this.molecularWeight + this.molecularWeightTolerance);

        String scoreTable = "";
        if (peaks != null && mzTolerance != null && scoreThreshold != null) {
            scoreTable += "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score, MAX(Error) AS Error FROM (\n";
            String finalSpectrumSelector = spectrumSelector;
            scoreTable += peaks.stream()
                    .map(p -> String.format(
                            "\tSELECT SpectrumId, SQRT(Intensity * %f) AS Product, ABS(MolecularWeight - %f) AS Error " +
                                    "FROM Spectrum INNER JOIN Peak ON Peak.SpectrumId = Spectrum.Id " +
                                    "WHERE %s AND Mz > %f AND Mz < %f\n",
                            p.getIntensity(), molecularWeight, finalSpectrumSelector,
                            p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                    .collect(Collectors.joining("\tUNION ALL\n"));
            scoreTable += ") AS SearchTable ";
            scoreTable += String.format("GROUP BY SpectrumId HAVING Score > %f\n", scoreThreshold);

        } else {
            scoreTable += String.format("\tSELECT Id AS SpectrumId, NULL AS Score, ABS(MolecularWeight - %f) AS Error " +
                            "FROM Spectrum WHERE %s\n",
                    molecularWeight, spectrumSelector);
        }
        return scoreTable;
    }
}
