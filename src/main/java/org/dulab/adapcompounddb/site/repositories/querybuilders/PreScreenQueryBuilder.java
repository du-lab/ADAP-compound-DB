package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.exceptions.IllegalSpectrumSearchException;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreScreenQueryBuilder {

    private enum SearchType {
        CONSENSUS("Spectrum.Consensus = 1"),
        REFERENCE("Spectrum.Reference = 1"),
        CLUSTERABLE("Spectrum.Clusterable = 1");

        private final String query;

        SearchType(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }

//    private final String spectrumTypeQuery;
    private final Set<BigInteger> submissionIds;
    private final boolean searchConsensus;
    private final boolean searchReference;
    private final boolean searchClusterable;

    private Set<ChromatographyType> chromatographyTypes;


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
        this.searchConsensus = searchConsensus;
        this.searchReference = searchReference;
        this.searchClusterable = searchClusterable;

//        spectrumTypeQuery = Arrays.stream(
//                new String[]{
//                        searchConsensus ? "Spectrum.Consensus  = 1" : null,
//                        searchReference ? "Spectrum.Reference = 1" : null,
//                        searchClusterable ? "Spectrum.Clusterable = 1" : null})
//                .filter(Objects::nonNull).collect(Collectors.joining(" OR "));
    }


    public PreScreenQueryBuilder withChromatographyTypes(ChromatographyType... chromatographyTypes) {
        this.chromatographyTypes = Arrays.stream(chromatographyTypes).collect(Collectors.toSet());
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

    public String buildQueryBlock(int numberOfTopMz, Double queryMz, SearchType searchType) {

        String queryBlock = String.format("SELECT Spectrum.Id FROM Spectrum " +
                "LEFT JOIN File ON File.Id = Spectrum.FileId " +
                "LEFT JOIN Submission ON Submission.Id = File.SubmissionId " +
                "LEFT JOIN UserPrincipal ON UserPrincipal.Id = Submission.UserPrincipalId\n" +
                "LEFT JOIN Identifier ON Spectrum.Id = Identifier.SpectrumId "+
                "WHERE %s", searchType.getQuery());

        if (chromatographyTypes != null){
            if(searchMassLibrary) {
                chromatographyTypes.add(ChromatographyType.NONE);
            }
            String types = chromatographyTypes.stream()
                    .map(ChromatographyType::toString)
                    .collect(Collectors.joining("','", "'", "'"));
            queryBlock += String.format(" AND Spectrum.ChromatographyType IN (%s)", types);
        }

        String whereBlock = "";

        if(Identifier != null && !Identifier.trim().isEmpty())
            whereBlock += String.format(" AND (Spectrum.Name LIKE '%%%1$s%%' " +
                    "OR Spectrum.InChiKey = '%1$s' " +
                    "OR Identifier.Value = '%1$s')", Identifier);

        if (precursorMz != null && precursorTolerance != null)
            whereBlock += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    precursorMz - precursorTolerance, precursorMz + precursorTolerance);

        if (precursorMz != null && precursorTolerancePPM != null)
            whereBlock += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    getLowerLimit(precursorMz, precursorTolerancePPM),
                    getUpperLimit(precursorMz, precursorTolerancePPM));

        if (masses != null && massTolerance != null)
            whereBlock += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.Mass > %f AND Spectrum.Mass < %f)",
                            mass - massTolerance, mass + massTolerance))
                    .collect(Collectors.joining(" OR ")));

        if (masses != null && massTolerancePPM != null)
            whereBlock += String.format(" AND (%s)", Arrays.stream(masses)
                    .mapToObj(mass -> String.format(
                            "(Spectrum.Mass > %f AND Spectrum.Mass < %f)",
                            getLowerLimit(mass, massTolerancePPM), getUpperLimit(mass, massTolerancePPM)))
                    .collect(Collectors.joining(" OR ")));

        if (retTime != null && retTimeTolerance != null)
            whereBlock += String.format(" AND Spectrum.RetentionTime > %f AND Spectrum.RetentionTime < %f",
                    retTime - retTimeTolerance, retTime + retTimeTolerance);

        if (retIndex != null && retIndexTolerance != null)
            whereBlock += String.format(" AND Spectrum.RetentionIndex > %f AND Spectrum.RetentionIndex < %f",
                    retIndex - retIndexTolerance, retIndex + retIndexTolerance);

        if (querySpectrum != null && mzTolerance != null) {
            whereBlock += String.format(" AND (%s)", IntStream.range(1, numberOfTopMz + 1)
                    .mapToObj(i -> String.format("(TopMz%d > %f AND TopMz%d < %f)",
                            i, queryMz - mzTolerance,
                            i, queryMz + mzTolerance))
                    .collect(Collectors.joining(" OR ")));
        }

        if (querySpectrum != null && mzTolerancePPM != null) {
            whereBlock += String.format(" AND (%s)", IntStream.range(1, numberOfTopMz + 1)
                    .mapToObj(i -> String.format("(TopMz%d > %f AND TopMz%d < %f)",
                            i, getLowerLimit(queryMz, mzTolerancePPM),
                            i, getUpperLimit(queryMz, mzTolerancePPM)))
                    .collect(Collectors.joining(" OR ")));
        }

        if (whereBlock.isEmpty())
            throw new IllegalSpectrumSearchException();

        queryBlock += getPrivacyConditions(user, whereBlock);

        if (submissionIds != null)
            queryBlock += String.format(" AND (%s)", buildConditionStringWithSubmissionIds(searchType));

        queryBlock += "\n";
        return queryBlock;
    }

    private String getPrivacyConditions(final UserPrincipal user,final String whereBlock) {
        return String.format("%s AND (Spectrum.FileId IS NULL OR Submission.IsPrivate IS FALSE%s)",
                whereBlock,
                user != null ? user.getOrganizationId() != null ?
                        " OR UserPrincipal.Id = " + user.getId()
                                + " OR UserPrincipal.Id = " + user.getOrganizationUser().getId()
                        : " OR UserPrincipal.Id = " + user.getId() : "");
    }

    public String build() {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) as Common, TempTable.Id FROM (\n");

        List<SearchType> searchTypes = getSearchTypes();
        if (searchTypes.isEmpty())
            throw new IllegalStateException(
                    "You must search against consensus, reference, or clusterable spectra. None is specified.");

        for (int i = 0; i < searchTypes.size(); i++) {
            if (i > 0)
                query.append("union all\n");

            SearchType searchType = searchTypes.get(i);
            if (querySpectrum != null && mzTolerance != null) {
                if (querySpectrum.getTopMz1() != null) {
                    query.append(buildQueryBlock(8, querySpectrum.getTopMz1(), searchType));
                }
                if (querySpectrum.getTopMz2() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(9, querySpectrum.getTopMz2(), searchType));
                }
                if (querySpectrum.getTopMz3() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(10, querySpectrum.getTopMz3(), searchType));
                }
                if (querySpectrum.getTopMz4() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(11, querySpectrum.getTopMz4(), searchType));
                }
                if (querySpectrum.getTopMz5() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(12, querySpectrum.getTopMz5(), searchType));
                }
                if (querySpectrum.getTopMz6() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(13, querySpectrum.getTopMz6(), searchType));
                }
                if (querySpectrum.getTopMz7() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(14, querySpectrum.getTopMz7(), searchType));
                }
                if (querySpectrum.getTopMz8() != null) {
                    query.append("union all\n");
                    query.append(buildQueryBlock(15, querySpectrum.getTopMz8(), searchType));
                }

            } else {
                query.append(buildQueryBlock(0, null, searchType));
            }
        }
        query.append(") AS TempTable\n");

        query.append("\nGROUP BY Id ORDER BY Common DESC");

        return query.toString();
    }

    private List<SearchType> getSearchTypes() {
        List<SearchType> searchTypes = new ArrayList<>();
        if (searchConsensus)
            searchTypes.add(SearchType.CONSENSUS);
        if (searchReference)
            searchTypes.add(SearchType.REFERENCE);
        if (searchClusterable)
            searchTypes.add(SearchType.CLUSTERABLE);
        return searchTypes;
    }

    private String buildConditionStringWithSubmissionIds(SearchType searchType) {

        if (submissionIds == null)
            return null;

        String ids = submissionIds.stream()
                .filter(x -> !x.equals(BigInteger.ZERO))
                .map(BigInteger::toString)
                .collect(Collectors.joining(","));

        String condition;
//        if (submissionIds.contains(BigInteger.ZERO) && !ids.isEmpty())
//            condition = String.format("Spectrum.Consensus = 1 OR Submission.Id IN (%s)", ids);
        if (searchType == SearchType.CONSENSUS && submissionIds.contains(BigInteger.ZERO))
            condition = "Spectrum.Consensus = 1";
        else if (searchType == SearchType.REFERENCE && !ids.isEmpty())
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
