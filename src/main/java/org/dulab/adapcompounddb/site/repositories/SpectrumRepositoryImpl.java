package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.site.repositories.querybuilders.FilterQueryBuilder;
import org.dulab.adapcompounddb.site.repositories.querybuilders.PreScreenQueryBuilder;
import org.dulab.adapcompounddb.site.repositories.querybuilders.SpectrumQueryBuilder;
import org.dulab.adapcompounddb.site.repositories.querybuilders.SpectrumQueryBuilderAlt;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;


public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumRepositoryImpl.class);

    private static final int MAX_PEAKS_IN_QUERY = 1000000;
    private static final int MAX_PROPERTIES_IN_QUERY = 1000000;

    private static final String PEAK_INSERT_SQL_STRING = "INSERT INTO `Peak`(`Mz`, `Intensity`, `SpectrumId`) VALUES ";
    private static final String PROPERTY_INSERT_SQL_STRING = "INSERT INTO `SpectrumProperty`(`SpectrumId`, `Name`, `Value`) VALUES ";
    private static final String PEAK_VALUE_SQL_STRING = "(%f,%f,%d)";
    private static final String PROPERTY_VALUE_SQL_STRING = "(%d, %s, %s)";

    public static final String DOUBLE_QUOTE = "\"";
    public static final String COMMA = ",";


    private static final SqlField[] spectrumFields = new SqlField[]{
            new SqlField("Name", "%s", s -> quote(s.getName())),
            new SqlField("ExternalId", "%s", s -> quote(s.getExternalId())),
            new SqlField("Precursor", "%f", Spectrum::getPrecursor),
            new SqlField("PrecursorType", "%s", s -> quote(s.getPrecursorType())),
            new SqlField("RetentionTime", "%f", Spectrum::getRetentionTime),
            new SqlField("Significance", "%f", Spectrum::getSignificance),
            new SqlField("ClusterId", "%d", s -> s.getCluster() != null ? s.getCluster().getId() : null),
            new SqlField("Consensus", "%b", Spectrum::isConsensus),
            new SqlField("Reference", "%b", Spectrum::isReference),
            new SqlField("InHouseReference", "%b", Spectrum::isInHouseReference),
            new SqlField("IntegerMz", "%b", Spectrum::isIntegerMz),
            new SqlField("ChromatographyType", "%s", s -> quote(s.getChromatographyType().name())),
            new SqlField("FileId", "%d", s -> s.getFile().getId()),
            new SqlField("Mass", "%f", Spectrum::getMass),
            new SqlField("Formula", "%s", s -> quote(s.getFormula())),
            new SqlField("CanonicalSMILES", "%s", s -> quote(s.getCanonicalSmiles())),
            new SqlField("InChi", "%s", s -> quote(s.getInChi())),
            new SqlField("InChiKey", "%s", s -> quote(s.getInChiKey())),
            new SqlField("TopMz1", "%f", Spectrum::getTopMz1),
            new SqlField("TopMz2", "%f", Spectrum::getTopMz2),
            new SqlField("TopMz3", "%f", Spectrum::getTopMz3),
            new SqlField("TopMz4", "%f", Spectrum::getTopMz4),
            new SqlField("TopMz5", "%f", Spectrum::getTopMz5),
            new SqlField("TopMz6", "%f", Spectrum::getTopMz6),
            new SqlField("TopMz7", "%f", Spectrum::getTopMz7),
            new SqlField("TopMz8", "%f", Spectrum::getTopMz8),
            new SqlField("TopMz9", "%f", Spectrum::getTopMz9),
            new SqlField("TopMz10", "%f", Spectrum::getTopMz10),
            new SqlField("TopMz11", "%f", Spectrum::getTopMz11),
            new SqlField("TopMz12", "%f", Spectrum::getTopMz12),
            new SqlField("TopMz13", "%f", Spectrum::getTopMz13),
            new SqlField("TopMz14", "%f", Spectrum::getTopMz14),
            new SqlField("TopMz15", "%f", Spectrum::getTopMz15),
            new SqlField("TopMz16", "%f", Spectrum::getTopMz16)
    };


    @PersistenceContext
    private EntityManager entityManager;

    @Deprecated
    @Override
    public List<SpectrumMatch> spectrumSearch(final SearchType searchType, final Spectrum querySpectrum, final QueryParameters params) {

        final SpectrumQueryBuilder queryBuilder = new SpectrumQueryBuilder(
                searchType, querySpectrum.getChromatographyType(), params.getExcludeSpectra());

        if (params.getScoreThreshold() != null && params.getMzTolerance() != null) {
            queryBuilder.setSpectrum(querySpectrum, params.getMzTolerance(), params.getScoreThreshold());
        }

        if (querySpectrum.getPrecursor() != null && params.getPrecursorTolerance() != null) {
            queryBuilder.setPrecursorRange(querySpectrum.getPrecursor(), params.getPrecursorTolerance());
        }

        if (querySpectrum.getRetentionTime() != null && params.getRetTimeTolerance() != null) {
            queryBuilder.setRetentionTimeRange(querySpectrum.getRetentionTime(), params.getRetTimeTolerance());
        }

        queryBuilder.setTags(params.getTags());

        final String sqlQuery = queryBuilder.build();


        @SuppressWarnings("unchecked") final List<Object[]> resultList = entityManager  // .getEntityManagerFactory().createEntityManager()
                .createNativeQuery(sqlQuery, "SpectrumScoreMapping")
                .getResultList();

        final List<SpectrumMatch> matches = new ArrayList<>();
        for (final Object[] objects : resultList) {
            final long matchSpectrumId = (long) objects[0];
            final double score = (double) objects[1];

            final SpectrumMatch match = new SpectrumMatch();
            match.setQuerySpectrum(querySpectrum);
            match.setMatchSpectrum(entityManager.find(Spectrum.class, matchSpectrumId));
            match.setScore(score);
            matches.add(match);
        }

        return matches;
    }

    @Override
    public Iterable<SpectrumClusterView> matchAgainstConsensusAndReferenceSpectra(
            List<BigInteger> spectrumIds,
            @NotNull Iterable<BigInteger> submissionIds, Spectrum querySpectrum, SearchParameters parameters) {

        return searchSpectra(spectrumIds, submissionIds, querySpectrum, parameters,
                true, true, false,
                SpectrumClusterView.class);
//        for (SpectrumClusterView match : matches) {
//            if (match.getMassError() == null && match.getMassErrorPPM() == null && parameters.getMasses() != null) {
//                match.
//            }
//        }
    }

    @Override
    public Iterable<SpectrumMatch> matchAgainstClusterableSpectra(
            List<BigInteger> spectrumIds, Iterable<BigInteger> submissionIds, Spectrum querySpectrum, SearchParameters parameters) {

        Iterable<SpectrumMatch> matches = searchSpectra(spectrumIds, submissionIds, querySpectrum, parameters,
                false, false, true, SpectrumMatch.class);
        matches.forEach(m -> m.setQuerySpectrum(querySpectrum));
        return matches;
    }

    private <E> Iterable<E> searchSpectra(List<BigInteger> spectrumIds,
                                          @NotNull Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
                                          SearchParameters parameters, boolean searchConsensusSpectra, boolean searchReferenceSpectra,
                                          boolean searchClusterableSpectra, Class<E> classOfE) {

        List<BigInteger> submissionIdList = new ArrayList<>();
        submissionIds.forEach(submissionIdList::add);

        SpectrumQueryBuilderAlt builder = new SpectrumQueryBuilderAlt(spectrumIds, submissionIdList, parameters.getLimit(),
                searchConsensusSpectra, searchReferenceSpectra, searchClusterableSpectra);
        if (querySpectrum != null) {
            builder = builder.withChromatographyType(querySpectrum.getChromatographyType())
                    .withQuerySpectrum(querySpectrum.getPeaks(), parameters.getMzTolerance(), parameters.getScoreThreshold())
                    .withPrecursor(querySpectrum.getPrecursor(), parameters.getPrecursorTolerance())
                    .withRetTime(querySpectrum.getRetentionTime(), parameters.getRetTimeTolerance());
            if (querySpectrum.getMass() != null) {
                builder = builder.withMass(querySpectrum.getMass(), parameters.getMassTolerance())
                        .withMassPPM(querySpectrum.getMass(), parameters.getMassTolerancePPM());
            } else {
                builder = builder.withMasses(parameters.getMasses(), parameters.getMassTolerance())
                        .withMassesPPM(parameters.getMasses(), parameters.getMassTolerancePPM());
            }
        }

        String query;
        try {
            if (classOfE == SpectrumClusterView.class)
                query = builder.buildSpectrumClusterViewQuery();
            else if (classOfE == SpectrumMatch.class)
                query = builder.buildSpectrumMatchQuery();
            else
                throw new IllegalStateException("Unknown class: " + classOfE);

        } catch (QueryBuilderException e) {
            LOGGER.warn(e.getMessage());
            return new ArrayList<>(0);
        }

        @SuppressWarnings("unchecked")
        List<E> resultList = entityManager
                .createNativeQuery(query, classOfE)
                .getResultList();

        return resultList;
    }

    private String[] generateQueriesToSavePeaks(List<Spectrum> spectrumList, List<Long> savedSpectrumIds) {

        List<List<String>> peakTriplesList = new ArrayList<>();
        List<String> peakTriples = new ArrayList<>();
        int peakCount = 0;
        for (int i = 0; i < spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();

            if (peaks == null)
                continue;

            for (Peak peak : peaks) {
                peakTriples.add(String.format("(%f, %f, %d)", peak.getMz(), peak.getIntensity(), savedSpectrumIds.get(i)));
            }

            peakCount += peaks.size();

            if (peakCount > MAX_PEAKS_IN_QUERY) {
                peakTriplesList.add(peakTriples);
                peakTriples = new ArrayList<>();
                peakCount = 0;
            }
        }

        if (peakCount > 0)
            peakTriplesList.add(peakTriples);

        return peakTriplesList.stream()
                .map(triples -> PEAK_INSERT_SQL_STRING + String.join(",", triples))
                .toArray(String[]::new);
    }

    private String[] generateQueriesToSaveProperties(List<Spectrum> spectrumList, List<Long> savedSpectrumIds) {

        List<List<String>> propertyTriplesList = new ArrayList<>();
        List<String> propertyTriples = new ArrayList<>();
        int propertyCount = 0;
        for (int i = 0; i < spectrumList.size(); i++) {
            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();

            if (properties == null)
                continue;

            for (SpectrumProperty property : properties) {
                propertyTriples.add(String.format("(%d, \"%s\", \"%s\")",
                        savedSpectrumIds.get(i),
                        property.getName().replace("\"", "\"\""),
                        property.getValue().replace("\"", "\"\"")));
            }

            propertyCount += properties.size();

            if (propertyCount > MAX_PROPERTIES_IN_QUERY) {
                propertyTriplesList.add(propertyTriples);
                propertyTriples = new ArrayList<>();
                propertyCount = 0;
            }
        }

        if (propertyCount > 0)
            propertyTriplesList.add(propertyTriples);

        return propertyTriplesList.stream()
                .map(triples -> PROPERTY_INSERT_SQL_STRING + String.join(",", triples))
                .toArray(String[]::new);
    }

    @Override
    public void savePeaksAndPropertiesQuery(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {

        String[] peakSqls = generateQueriesToSavePeaks(spectrumList, savedSpectrumIdList);
        for (String peakSql : peakSqls) {
            if (!peakSql.equals(PEAK_INSERT_SQL_STRING)) {
                LOGGER.info(String.format("Saving peaks to the database (%d bytes)...", peakSql.length() * 2));
                Query peakQuery = entityManager.createNativeQuery(peakSql);
                peakQuery.executeUpdate();
            }
        }

        String[] propertySqls = generateQueriesToSaveProperties(spectrumList, savedSpectrumIdList);
        for (String propertySql : propertySqls) {
            if (!propertySql.equals(PROPERTY_INSERT_SQL_STRING)) {
                LOGGER.info(String.format("Saving spectrum properties to the database (%d bytes)...", propertySql.length() * 2));
                Query propertyQuery = entityManager.createNativeQuery(propertySql);
                propertyQuery.executeUpdate();
            }
        }
    }

    @Override
    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList) {

        final List<Spectrum> spectrumList = new ArrayList<>();

        StringBuilder insertSql = new StringBuilder(
                String.format("INSERT INTO `Spectrum`(%s) VALUES ",
                        Arrays.stream(spectrumFields)
                                .map(SqlField::getName)
                                .map(x -> String.format("`%s`", x))
                                .collect(Collectors.joining(", "))));

        int count = 0;
        for (int i = 0; i < fileList.size(); i++) {
            final List<Spectrum> spectra = fileList.get(i).getSpectra();
            if (spectra == null) continue;
            spectrumList.addAll(spectra);

            for (int j = 0; j < spectra.size(); j++) {
                if (i != 0 || j != 0) {
                    insertSql.append(COMMA);
                }
                final Spectrum spectrum = spectra.get(j);
                spectrum.setFile(fileList.get(i));

                insertSql.append(String.format("(%s)",
                        Arrays.stream(spectrumFields)
                                .map(field -> String.format(field.format, field.function.apply(spectrum)))
                                .collect(Collectors.joining(", "))));

                count++;
            }
        }

        LOGGER.info(String.format("Saving %d spectra to the database...", count));

        final Query insertQuery = entityManager.createNativeQuery(insertSql.toString());
        insertQuery.executeUpdate();

        final List<Long> fileIds = new ArrayList<>(fileList.size());
        fileList.forEach(file -> fileIds.add(file.getId()));
        final String selectSql = "select s.id from Spectrum s where s.file.id in (:fileIds)";

        final TypedQuery<Long> selectQuery = entityManager.createQuery(selectSql, Long.class);
        selectQuery.setParameter("fileIds", fileIds);

        final List<Long> spectrumIds = selectQuery.getResultList();

        savePeaksAndPropertiesQuery(spectrumList, spectrumIds);
    }

    @Override
    public void savePeaksAndProperties(final Long spectrumId, final List<Peak> peaks, final List<SpectrumProperty> properties) {
        final StringBuilder peakSql = new StringBuilder("INSERT INTO `Peak` (`Mz`,`Intensity`,`SpectrumId`) VALUES ");
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `SpectrumProperty` (`SpectrumId`,`Name`,`Value`) VALUES ");

        for (int j = 0; j < peaks.size(); j++) {
            if (j != 0) {
                peakSql.append(COMMA);
            }
            final Peak p = peaks.get(j);

            peakSql.append(String.format(PEAK_VALUE_SQL_STRING, p.getMz(), p.getIntensity(), spectrumId));
        }

        for (int j = 0; j < properties.size(); j++) {
            if (j != 0) {
                propertySql.append(COMMA);
            }
            final SpectrumProperty sp = properties.get(j);
            propertySql.append(String.format(PROPERTY_VALUE_SQL_STRING, spectrumId, DOUBLE_QUOTE + sp.getName() + DOUBLE_QUOTE, DOUBLE_QUOTE + sp.getValue() + DOUBLE_QUOTE));
        }

        entityManager.flush();
        entityManager.clear();
        final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
        peakQuery.executeUpdate();
        final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
        propertyQuery.executeUpdate();
    }

    /**
     * Returns Spectrum IDs and the number of common m/z peaks
     *
     * @param querySpectrum     query spectrum
     * @param params            search parameters
     * @param greedy            if true, then all spectra are returned without matching the top m/z values
     * @param searchConsensus   if true, then consensus spectra are returned
     * @param searchReference   if true, then reference spectra are returned
     * @param searchClusterable if true then clusterable spectra are returned
     * @return collection of Spectrum IDs and the number of common m/z peaks
     */
    @Override
    public Iterable<Object[]> preScreenSpectra(Spectrum querySpectrum, SearchParameters params, UserPrincipal user,
                                               boolean greedy, boolean searchConsensus, boolean searchReference,
                                               boolean searchClusterable) {

        PreScreenQueryBuilder queryBuilder =
                new PreScreenQueryBuilder(searchConsensus, searchReference, searchClusterable, params.getSubmissionIds())
                        .withUser(user)
                        .withChromatographyType(querySpectrum.getChromatographyType())
                        .withPrecursor(params.getPrecursorTolerance(), params.getPrecursorTolerancePPM(), querySpectrum.getPrecursor())
                        .withRetTime(params.getRetTimeTolerance(), querySpectrum.getRetentionTime());

        queryBuilder = (querySpectrum.getMass() != null)
                ? queryBuilder.withMass(params.getMassTolerance(), params.getMassTolerancePPM(), querySpectrum.getMass())
                : queryBuilder.withMass(params.getMassTolerance(), params.getMassTolerancePPM(), params.getMasses());

        if (!greedy)
            queryBuilder = queryBuilder.withQuerySpectrum(params.getMzTolerance(), params.getMzTolerancePPM(), querySpectrum);

        final String sqlQuery = queryBuilder.build();

        @SuppressWarnings("unchecked") final Iterable<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();

        return resultList;
    }

    @Override
    public Iterable<Object[]> filterSpectra(
            Map<BigInteger, List<BigInteger>> countToSpectrumIdMap, SearchParameters params) {

        FilterQueryBuilder builder = new FilterQueryBuilder(
                params.getSpecies(), params.getSource(), params.getDisease());

        String sqlQuery = builder.build(countToSpectrumIdMap);

        @SuppressWarnings("unchecked")
        Iterable<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();

        return resultList;
    }

    private static String quote(String x) {
        if (x == null) return null;
        return String.format("\"%s\"", x.replace("\"", "\"\""));
    }


    private static class SqlField {

        private final String name;
        private final String format;
        private final Function<Spectrum, Object> function;

        public SqlField(String name, String format, Function<Spectrum, Object> function) {
            this.name = name;
            this.format = format;
            this.function = function;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public Function<Spectrum, Object> getFunction() {
            return function;
        }
    }
}
