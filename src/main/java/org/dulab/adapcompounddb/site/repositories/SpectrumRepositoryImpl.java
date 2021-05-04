package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;


public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumRepositoryImpl.class);

    private static final String PEAK_INSERT_SQL_STRING = "INSERT INTO `Peak`(`Mz`, `Intensity`, `SpectrumId`) VALUES ";
    private static final String PROPERTY_INSERT_SQL_STRING = "INSERT INTO `SpectrumProperty`(`SpectrumId`, `Name`, `Value`) VALUES ";
    private static final String PEAK_VALUE_SQL_STRING = "(%f,%f,%d)";
    private static final String PROPERTY_VALUE_SQL_STRING = "(%d, %s, %s)";
    private static final String SPECTRUM_VALUE_SQL_STRING = "(%s, %f, %f, %f, %d, %b, %b, %b, %s, %d, %f, %f, %f, %f, %f, " +
            "%f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f)";

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
            if (querySpectrum.getMolecularWeight() != null) {
                builder = builder.withMass(querySpectrum.getMolecularWeight(), parameters.getMassTolerance())
                        .withMassPPM(querySpectrum.getMolecularWeight(), parameters.getMassTolerancePPM());
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
    public void savePeaksAndPropertiesQuery(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {
        final StringBuilder peakSql = new StringBuilder(PEAK_INSERT_SQL_STRING);
        final StringBuilder propertySql = new StringBuilder(PROPERTY_INSERT_SQL_STRING);

        for (int i = 0; i < spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();
            if (peaks != null) {
                for (int j = 0; j < peaks.size(); j++) {
                    if (i != 0 || j != 0) {
                        peakSql.append(COMMA);
                    }
                    final Peak peak = peaks.get(j);
                    peakSql.append(String.format("(%f, %f, %d)", peak.getMz(), peak.getIntensity(), savedSpectrumIdList.get(i)));
                }
            }

            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();
            if (properties != null) {
                for (int j = 0; j < properties.size(); j++) {
                    if (i != 0 || j != 0) {
                        propertySql.append(COMMA);
                    }
                    final SpectrumProperty property = properties.get(j);
                    propertySql.append(String.format("(%d, \"%s\", \"%s\")",
                            savedSpectrumIdList.get(i),
                            property.getName().replace("\"", "\"\""),
                            property.getValue().replace("\"", "\"\"")));
                }
            }
        }

        if (!peakSql.toString().equals(PEAK_INSERT_SQL_STRING)) {
            final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
            peakQuery.executeUpdate();
        }
        if (!propertySql.toString().equals(PROPERTY_INSERT_SQL_STRING)) {
            final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
            propertyQuery.executeUpdate();
        }
    }

    @Override
    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList) {
        final List<Spectrum> spectrumList = new ArrayList<>();

        final StringBuilder insertSql = new StringBuilder("INSERT INTO `Spectrum`(" +
                "`Name`, `Precursor`, `PrecursorType`, `RetentionTime`, `Significance`, " +
                "`ClusterId`, `Consensus`, `Reference`, `IntegerMz`, " +
                "`ChromatographyType`, `FileId`, `MolecularWeight`, " +
                "`TopMz1`, `TopMz2`, `TopMz3`, `TopMz4`, `TopMz5`, `TopMz6`, `TopMz7`, `TopMz8`, `TopMz9`, " +
                "`TopMz10`, `TopMz11`, `TopMz12`, `TopMz13`, `TopMz14`, `TopMz15`, `TopMz16`" +
                ") VALUES ");

        for (int i = 0; i < fileList.size(); i++) {
            final List<Spectrum> spectra = fileList.get(i).getSpectra();
            if (spectra == null) continue;
            spectrumList.addAll(spectra);
            for (int j = 0; j < spectra.size(); j++) {
                if (i != 0 || j != 0) {
                    insertSql.append(COMMA);
                }
                final Spectrum spectrum = spectra.get(j);

                insertSql.append(String.format(SPECTRUM_VALUE_SQL_STRING,
                        spectrum.getName() != null
                                ? String.format("\"%s\"", spectrum.getName().replace("\"", "\"\""))
                                : null,
                        spectrum.getPrecursor(),
                        spectrum.getPrecursorType() != null ? String.format("\"%s\"", spectrum.getPrecursorType()) : null,
                        spectrum.getRetentionTime(),
                        spectrum.getSignificance(),
                        spectrum.getCluster() != null ? spectrum.getCluster().getId() : null,
                        spectrum.isConsensus(),
                        spectrum.isReference(),
                        spectrum.isIntegerMz(),
                        String.format("\"%s\"", spectrum.getChromatographyType().name()),
                        savedFileIdList.get(i),
                        spectrum.getMolecularWeight(),
                        spectrum.getTopMz1(),
                        spectrum.getTopMz2(),
                        spectrum.getTopMz3(),
                        spectrum.getTopMz4(),
                        spectrum.getTopMz5(),
                        spectrum.getTopMz6(),
                        spectrum.getTopMz7(),
                        spectrum.getTopMz8(),
                        spectrum.getTopMz9(),
                        spectrum.getTopMz10(),
                        spectrum.getTopMz11(),
                        spectrum.getTopMz12(),
                        spectrum.getTopMz13(),
                        spectrum.getTopMz14(),
                        spectrum.getTopMz15(),
                        spectrum.getTopMz16()
                ));
            }
        }
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
     * @param parameters        search parameters
     * @param greedy            if true, then all spectra are returned without matching the top m/z values
     * @param searchConsensus   if true, then consensus spectra are returned
     * @param searchReference   if true, then reference spectra are returned
     * @param searchClusterable if true then clusterable spectra are returned
     * @return collection of Spectrum IDs and the number of common m/z peaks
     */
    @Override
    public Iterable<Object[]> preScreenSpectra(Spectrum querySpectrum, SearchParameters parameters, boolean greedy,
                                               boolean searchConsensus, boolean searchReference,
                                               boolean searchClusterable) {

        PreScreenQueryBuilder queryBuilder =
                new PreScreenQueryBuilder(searchConsensus, searchReference, searchClusterable)
                        .withChromatographyType(querySpectrum.getChromatographyType())
                        .withPrecursor(querySpectrum.getPrecursor(), parameters.getPrecursorTolerance())
                        .withRetTime(querySpectrum.getRetentionTime(), parameters.getRetTimeTolerance())
                        .withMass(querySpectrum.getMolecularWeight(), parameters.getMassTolerance())
                        .withMassPPM(querySpectrum.getMolecularWeight(), parameters.getMassTolerancePPM());

        if (!greedy)
            queryBuilder = queryBuilder.withQuerySpectrum(querySpectrum, parameters.getMzTolerance());

        final String sqlQuery = queryBuilder.build();

        @SuppressWarnings("unchecked") final Iterable<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();

        return resultList;
    }
}
