package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreScreenQueryBuilder {

    private final String spectrumTypeQuery;
    private final Set<BigInteger> submissionIds;

    private List<ChromatographyType> chromatographyTypes;


    private UserPrincipal user = null;

    private Spectrum querySpectrum = null;
    private Double mzTolerance = null;
    private Integer mzTolerancePPM = null;

    private Double precursorMz = null;
    private Double precursorTolerance = null;
    private Integer precursorTolerancePPM = null;

    private Double retTime = null;
    private Double retTimeTolerance = null;

    private Double retIndex = null;
    private Double retIndexTolerance = null;

    private double[] masses = null;
    private Double massTolerance = null;
    private Integer massTolerancePPM = null;
    private String Identifier = null;
    private boolean searchMassLibrary = true;


    public PreScreenQueryBuilder(boolean searchConsensus, boolean searchReference, boolean searchClusterable,
                                 @Nullable Set<BigInteger> submissionIds) {

        this.submissionIds = submissionIds;

        spectrumTypeQuery = Arrays.stream(
                new String[]{
                        searchConsensus ? "Spectrum.Consensus IS TRUE" : null,
                        searchReference ? "Spectrum.Reference IS TRUE" : null,
                        searchClusterable ? "Spectrum.Clusterable IS TRUE" : null})
                .filter(Objects::nonNull).collect(Collectors.joining(" OR "));
    }


    public PreScreenQueryBuilder withChromatographyTypes(ChromatographyType... chromatographyTypes) {
        this.chromatographyTypes = List.of(chromatographyTypes);
        return this;
    }

    public PreScreenQueryBuilder withPrecursor(Double tolerance, Integer ppm, Double mz) {
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

    public PreScreenQueryBuilder withRetIndex(Double tolerance, Double retIndex) {
        this.retIndex = retIndex;
        this.retIndexTolerance = tolerance;
        return this;
    }

    public PreScreenQueryBuilder withMass(Double tolerance, Integer ppm, double... masses) {
        this.masses = masses;
        this.massTolerance = tolerance;
        this.massTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withQuerySpectrum(Double mzTolerance, Integer ppm, Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
        this.mzTolerance = mzTolerance;
        this.mzTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withUser(UserPrincipal user) {
        this.user = user;
        return this;
    }

    public PreScreenQueryBuilder withID(String id) {
        this.Identifier = id;
        return this;
    }

    public PreScreenQueryBuilder withSearchMassLibrary(boolean searchMassLibrary) {
        this.searchMassLibrary = searchMassLibrary;
        return this;
    }


    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    public String buildQueryBlock(int numberOfTopMz, Double queryMz) {

        String queryBlock = String.format("SELECT Spectrum.Id FROM Spectrum LEFT JOIN File ON File.Id = Spectrum.FileId " +
                "LEFT JOIN Submission ON Submission.Id = File.SubmissionId " +
                "LEFT JOIN UserPrincipal ON UserPrincipal.Id = Submission.UserPrincipalId\n" +
                "WHERE (%s)", spectrumTypeQuery);

        if (chromatographyTypes != null){
            if(searchMassLibrary) {
                chromatographyTypes.add(ChromatographyType.NONE);
            }
            String types = chromatographyTypes.stream()
                    .map(ChromatographyType::toString)
                    .collect(Collectors.joining("','", "'", "'"));
            queryBlock += String.format(" AND Spectrum.ChromatographyType IN (%s)", types);
        }





        if(Identifier != null && !Identifier.trim().isEmpty())
            queryBlock += String.format(" AND Spectrum.Name = '%s'", Identifier);

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
                            "(Spectrum.Mass > %f AND Spectrum.Mass < %f)",
                            mass - massTolerance, mass + massTolerance))
                    .collect(Collectors.joining(" OR ")));

        if (masses != null && massTolerancePPM != null)
            queryBlock += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.Mass > %f AND Spectrum.Mass < %f)",
                            getLowerLimit(mass, massTolerancePPM), getUpperLimit(mass, massTolerancePPM)))
                    .collect(Collectors.joining(" OR ")));

        if (retTime != null && retTimeTolerance != null)
            queryBlock += String.format(" AND Spectrum.RetentionTime > %f AND Spectrum.RetentionTime < %f",
                    retTime - retTimeTolerance, retTime + retTimeTolerance);

        if (retIndex != null && retIndexTolerance != null)
            queryBlock += String.format(" AND Spectrum.RetentionIndex > %f AND Spectrum.RetentionIndex < %f",
                    retIndex - retIndexTolerance, retIndex + retIndexTolerance);

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

        queryBlock += String.format(" AND (Spectrum.FileId IS NULL OR Submission.IsPrivate IS FALSE%s)",
                user != null ? " OR UserPrincipal.Id = " + user.getId() : "");
        if (submissionIds != null)
            queryBlock += String.format(" AND (%s)", buildConditionStringWithSubmissionIds());

        queryBlock += "\n";
        System.out.println(queryBlock);
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

        query += "\nGROUP BY Id ORDER BY Common DESC";

        return query;
    }

    private String buildConditionStringWithSubmissionIds() {

        if (submissionIds == null)
            return null;

        String ids = submissionIds.stream()
                .filter(x -> !x.equals(BigInteger.ZERO))
                .map(BigInteger::toString)
                .collect(Collectors.joining(","));

        String condition;
        if (submissionIds.contains(BigInteger.ZERO) && !ids.isEmpty())
            condition = String.format("Spectrum.Consensus IS TRUE OR Submission.Id IN (%s)", ids);
        else if (submissionIds.contains(BigInteger.ZERO))
            condition = "Spectrum.Consensus IS TRUE";
        else if (!ids.isEmpty())
            condition = String.format("Submission.Id IN (%s)", ids);
        else
            condition = "1 = 0";

        return condition;
    }

    private static double getLowerLimit(double x, double ppm) {
        return x * (1 - ppm * 1E-6);
    }

    private static double getUpperLimit(double x, double ppm) {
        return x * (1 + ppm * 1E-6);
    }
}
