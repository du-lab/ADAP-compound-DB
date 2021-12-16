package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.site.repositories.querybuilders.*;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.RetIndexMatchType;


public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumRepositoryImpl.class);

    private static final String PEAK_VALUE_SQL_STRING = "(%f,%f,%d)";
    private static final String PROPERTY_VALUE_SQL_STRING = "(%d, %s, %s)";

    public static final String DOUBLE_QUOTE = "\"";
    public static final String COMMA = ",";


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

    @Override
    public void saveSpectra(List<File> fileList, List<Long> savedFileIdList) {

        SaveSpectraQueryBuilder queryBuilder = new SaveSpectraQueryBuilder(fileList);
        String insertSql = queryBuilder.build();
        List<Spectrum> spectrumList = queryBuilder.getSpectrumList();

        LOGGER.info(String.format("Saving %d spectra to the database...", spectrumList.size()));

        Query insertQuery = entityManager.createNativeQuery(insertSql);
        insertQuery.executeUpdate();

        List<Long> fileIds = new ArrayList<>(fileList.size());
        fileList.forEach(file -> fileIds.add(file.getId()));
        String selectSql = "select s.id from Spectrum s where s.file.id in (:fileIds)";

        TypedQuery<Long> selectQuery = entityManager.createQuery(selectSql, Long.class);
        selectQuery.setParameter("fileIds", fileIds);

        List<Long> spectrumIds = selectQuery.getResultList();

        String[] peakSqls = new SavePeaksQueryBuilder(spectrumList, spectrumIds).build();
        for (String peakSql : peakSqls) {
            LOGGER.info(String.format("Saving peaks to the database (%d bytes)...", peakSql.length() * 2));
            Query peakQuery = entityManager.createNativeQuery(peakSql);
            peakQuery.executeUpdate();
        }

        String[] propertySqls = new SavePropertiesQueryBuilder(spectrumList, spectrumIds).build();
        for (String propertySql : propertySqls) {
            LOGGER.info(String.format("Saving spectrum properties to the database (%d bytes)...", propertySql.length() * 2));
            Query propertyQuery = entityManager.createNativeQuery(propertySql);
            propertyQuery.executeUpdate();
        }

        String[] synonymSqls = new SaveSynonymsQueryBuilder(spectrumList, spectrumIds).build();
        for (String synonymSql : synonymSqls) {
            LOGGER.info(String.format("Saving spectrum synonyms to the database (%d bytes)...", synonymSql.length() * 2));
            Query synonymQuery = entityManager.createNativeQuery(synonymSql);
            synonymQuery.executeUpdate();
        }

        String[] identifierSqls = new SaveIdentifiersQueryBuilder(spectrumList, spectrumIds).build();
        for (String identifierSql : identifierSqls) {
            LOGGER.info(String.format("Saving spectrum identifiers to the database (%d bytes)...", identifierSql.length() * 2));
            Query identifierQuery = entityManager.createNativeQuery(identifierSql);
            identifierQuery.executeUpdate();
        }
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

        if (params.getRetIndexMatchType() == RetIndexMatchType.ALWAYS_MATCH)
            queryBuilder = queryBuilder.withRetIndex(params.getRetIndexTolerance(), querySpectrum.getRetentionIndex());

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

}
