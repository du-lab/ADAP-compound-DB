package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;

public class SpectrumQueryBuilderAlt {

    private final Collection<BigInteger> submissionIds;
    private final ChromatographyType chromatographyType;
    private final boolean searchConsensusSpectra;
    private final boolean searchReferenceSpectra;

    private Double precursorMz = null;
    private Double precursorTolerance = null;
    private Double retTime = null;
    private Double retTimeTolerance = null;
    private Spectrum spectrum = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;


    public SpectrumQueryBuilderAlt(Collection<BigInteger> submissionIds, ChromatographyType chromatographyType,
                                   boolean searchConsensusSpectra, boolean searchReferenceSpectra) {

        if (!searchConsensusSpectra && !searchReferenceSpectra)
            throw new IllegalArgumentException("Either 'searchConsensusSpectra' or 'searchReferenceSpectra' must be true");

        this.submissionIds = submissionIds;
        this.chromatographyType = chromatographyType;
        this.searchConsensusSpectra = searchConsensusSpectra;
        this.searchReferenceSpectra = searchReferenceSpectra;
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

    public SpectrumQueryBuilderAlt withQuerySpectrum(Spectrum spectrum, double mzTolerance, double scoreThreshold) {
        this.spectrum = spectrum;
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

        String query = "SELECT ConsensusSpectrum.Id, SpectrumCluster.Id AS ClusterId, ConsensusSpectrum.Name, COUNT(DISTINCT File.SubmissionId) AS Size, Score, ";
        query += "AVG(Spectrum.Significance) AS AverageSignificance, MIN(Spectrum.Significance) AS MinimumSignificance, ";
        query += "MAX(Spectrum.Significance) AS MaximumSignificance, ConsensusSpectrum.ChromatographyType FROM (\n";
        query += "SELECT ClusterId, POWER(SUM(Product), 2) AS Score FROM (\n";
        query += spectrum.getPeaks().stream()
                .map(p -> String.format("\tSELECT ClusterId, SQRT(Intensity * %f) AS Product " +
                                "FROM Peak INNER JOIN Spectrum ON Peak.SpectrumId = Spectrum.Id " +
                                "WHERE %s AND Peak.Mz > %f AND Peak.Mz < %f\n",
                        p.getIntensity(),
                        getSpectrumSelector(true, false),
                        p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                .collect(Collectors.joining("\tUNION ALL\n"));
        query += ") AS SearchTable ";
        query += String.format("GROUP BY ClusterId HAVING Score > %f\n", scoreThreshold);
        query += ") AS ScoreTable JOIN SpectrumCluster ON SpectrumCluster.Id = ClusterId\n";
        query += "JOIN Spectrum AS ConsensusSpectrum ON ConsensusSpectrum.Id = SpectrumCluster.ConsensusSpectrumId\n";
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

        String query = "SELECT Spectrum.Id, NULL AS ClusterId, Spectrum.Name, 1 AS Size, Score, ";
        query += "Spectrum.Significance AS AverageSignificance, Spectrum.Significance AS MinimumSignificance, ";
        query += "Spectrum.Significance AS MaximumSignificance, Spectrum.ChromatographyType FROM (\n";
        query += "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n";
        query += spectrum.getPeaks().stream()
                .map(p -> String.format("\tSELECT SpectrumId, SQRT(Intensity * %f) AS Product " +
                                "FROM Peak INNER JOIN Spectrum ON Peak.SpectrumId = Spectrum.Id " +
                                "WHERE %s AND Peak.Mz > %f AND Peak.Mz < %f\n",
                        p.getIntensity(),
                        getSpectrumSelector(false, true),
                        p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                .collect(Collectors.joining("\tUNION ALL\n"));
        query += ") AS SearchTable ";
        query += String.format("GROUP BY SpectrumId HAVING Score > %f\n", scoreThreshold);
        query += ") AS ScoreTable JOIN Spectrum ON Spectrum.Id = SpectrumId\n";
        query += "JOIN File ON File.Id = Spectrum.FileId\n";
        query += String.format("WHERE File.SubmissionId IN (%s)\n", submissionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

        return query;
    }

    private String buildEmptyQuery() {
        return "SELECT Id, NULL AS ClusterId, Name, 1 AS Size, 0 AS Score, " +
                "Significance AS AverageSignificance, Significance AS MinimumSignificance, " +
                "Significance AS MaximumSignificance, ChromatographyType FROM Spectrum WHERE FALSE";
    }

    /**
     * Return the condition for selecting spectra based on a chromatography type, consensus or reference spectrum, precursor, etc.
     * @param isConsensus true if spectra must be consensus spectra
     * @param isReference true if spectra must be reference spectra
     * @return SQL string with the condition
     */
    private String getSpectrumSelector(boolean isConsensus, boolean isReference) {
        String spectrumSelector = String.format(
                "Spectrum.ChromatographyType = '%s' AND Spectrum.Consensus IS %s AND Spectrum.Reference IS %s",
                this.chromatographyType, isConsensus, isReference);
        if (this.precursorMz != null && this.precursorTolerance != null)
            spectrumSelector += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    this.precursorMz - this.precursorTolerance,
                    this.precursorMz + this.precursorTolerance);
        return spectrumSelector;
    }
}
