package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreScreenQueryBuilder {

    private final String spectrumTypeQuery;

    private ChromatographyType chromatographyType;

    private UserPrincipal user = null;

    private Spectrum querySpectrum = null;
    private Double mzTolerance = null;
    private Double mzTolerancePPM = null;

    private Double precursorMz = null;
    private Double precursorTolerance = null;
    private Double precursorTolerancePPM = null;

    private Double retTime = null;
    private Double retTimeTolerance = null;

    private double[] masses = null;
    private Double massTolerance = null;
    private Double massTolerancePPM = null;


    public PreScreenQueryBuilder(boolean searchConsensus, boolean searchReference, boolean searchClusterable) {

        spectrumTypeQuery = Arrays.stream(
                new String[]{
                        searchConsensus ? "Consensus IS TRUE" : null,
                        searchReference ? "Reference IS TRUE" : null,
                        searchClusterable ? "Clusterable IS TRUE" : null})
                .filter(Objects::nonNull).collect(Collectors.joining(" OR "));
    }

    public PreScreenQueryBuilder withChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
        return this;
    }

    public PreScreenQueryBuilder withPrecursor(Double tolerance, Double ppm, Double mz) {
        this.precursorMz = mz;
        this.precursorTolerance = tolerance;
        this.precursorTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withRetTime(Double tolerance, Double retTime) {
        this.retTime = retTime;
        this.retTimeTolerance = tolerance;
        return this;
    }

    public PreScreenQueryBuilder withMass(Double tolerance, Double ppm, double... masses) {
        this.masses = masses;
        this.massTolerance = tolerance;
        this.massTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withQuerySpectrum(Double mzTolerance, Double ppm, Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
        this.mzTolerance = mzTolerance;
        this.mzTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withUser(UserPrincipal user) {
        this.user = user;
        return this;
    }


    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    public String buildQueryBlock(int numberOfTopMz, Double queryMz) {

        String queryBlock = String.format("SELECT Id FROM Spectrum WHERE (%s)", spectrumTypeQuery);

        if (chromatographyType != null)
            queryBlock += String.format(" AND Spectrum.ChromatographyType = '%s'", chromatographyType);

        if (precursorMz != null && precursorTolerance != null)
            queryBlock += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    precursorMz - precursorTolerance, precursorMz + precursorTolerance);

        if (precursorMz != null && precursorTolerancePPM != null)
            queryBlock += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    getLowerLimit(precursorMz, precursorTolerancePPM),
                    getUpperLimit(precursorMz, precursorTolerancePPM));

        if (masses != null && massTolerance != null)
            queryBlock += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f)",
                            mass - massTolerance, mass + massTolerance))
                    .collect(Collectors.joining(" OR ")));

        if (masses != null && massTolerancePPM != null)
            queryBlock += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f)",
                            getLowerLimit(mass, massTolerancePPM), getUpperLimit(mass, massTolerancePPM)))
                    .collect(Collectors.joining(" OR ")));

        if (retTime != null && retTimeTolerance != null)
            queryBlock += String.format(" AND Spectrum.RetentionTime > %f AND Spectrum.RetentionTime < %f",
                    retTime - retTimeTolerance, retTime + retTimeTolerance);

        if (querySpectrum != null && mzTolerance != null) {
            queryBlock += String.format(" AND (%s)", IntStream.range(1, numberOfTopMz + 1)
                    .mapToObj(i -> String.format("(TopMz%d > %f AND TopMz%d < %f)",
                            i, queryMz - mzTolerance,
                            i, queryMz + mzTolerance))
                    .collect(Collectors.joining(" OR ")));
        }

        if (querySpectrum != null && mzTolerancePPM != null) {
            queryBlock += String.format(" AND (%s)", IntStream.range(1, numberOfTopMz + 1)
                    .mapToObj(i -> String.format("(TopMz%d > %f AND TopMz%d < %f)",
                            i, getLowerLimit(queryMz, mzTolerancePPM),
                            i, getUpperLimit(queryMz, mzTolerancePPM)))
                    .collect(Collectors.joining(" OR ")));
        }

        queryBlock += "\n";

        return queryBlock;
    }

    public String build() {
        String query;
        query = "SELECT COUNT(*) as Common, TempTable.Id FROM (\n";

        if (querySpectrum != null && mzTolerance != null) {
            if (querySpectrum.getTopMz1() != null) {
                query += buildQueryBlock(8, querySpectrum.getTopMz1());
            }
            if (querySpectrum.getTopMz2() != null) {
                query += "union all\n";
                query += buildQueryBlock(9, querySpectrum.getTopMz2());
            }
            if (querySpectrum.getTopMz3() != null) {
                query += "union all\n";
                query += buildQueryBlock(10, querySpectrum.getTopMz3());
            }
            if (querySpectrum.getTopMz4() != null) {
                query += "union all\n";
                query += buildQueryBlock(11, querySpectrum.getTopMz4());
            }
            if (querySpectrum.getTopMz5() != null) {
                query += "union all\n";
                query += buildQueryBlock(12, querySpectrum.getTopMz5());
            }
            if (querySpectrum.getTopMz6() != null) {
                query += "union all\n";
                query += buildQueryBlock(13, querySpectrum.getTopMz6());
            }
            if (querySpectrum.getTopMz7() != null) {
                query += "union all\n";
                query += buildQueryBlock(14, querySpectrum.getTopMz7());
            }
            if (querySpectrum.getTopMz8() != null) {
                query += "union all\n";
                query += buildQueryBlock(15, querySpectrum.getTopMz8());
            }

        } else {
            query += buildQueryBlock(0, null);
        }
        query += ") AS TempTable\n";

        query += "JOIN Spectrum ON Spectrum.Id = TempTable.Id ";
        query += "LEFT JOIN File ON File.Id = Spectrum.FileId ";
        query += "LEFT JOIN Submission ON Submission.Id = File.SubmissionId ";
        query += "LEFT JOIN UserPrincipal ON UserPrincipal.Id = Submission.UserPrincipalId\n";
        query += "WHERE Spectrum.FileId IS NULL OR Submission.IsPrivate IS FALSE";
        if (user != null)
            query += String.format(" OR UserPrincipal.Id = %d", user.getId());

        query += "\nGROUP BY Id ORDER BY Common DESC";

        return query;
    }

    private static double getLowerLimit(double x, double ppm) {
        return x * (1 - ppm * 1E-6);
    }

    private static double getUpperLimit(double x, double ppm) {
        return x * (1 + ppm * 1E-6);
    }
}
