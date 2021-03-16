package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

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
            @NotNull Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
            Double scoreThreshold, Double mzTolerance, Double precursorTolerance, Double molecularWeightTolerance) {

        return searchSpectra(submissionIds, querySpectrum,
                scoreThreshold, mzTolerance, precursorTolerance, molecularWeightTolerance,
                true, true, false,
                SpectrumClusterView.class);
    }

    @Override
    public Iterable<SpectrumMatch> matchAgainstClusterableSpectra(
            @NotNull Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
            Double scoreThreshold, Double mzTolerance, Double precursorTolerance, Double molecularWeightTolerance) {

        Iterable<SpectrumMatch> matches = searchSpectra(submissionIds, querySpectrum,
                scoreThreshold, mzTolerance, precursorTolerance, molecularWeightTolerance,
                false, false, true, SpectrumMatch.class);
        matches.forEach(m -> m.setQuerySpectrum(querySpectrum));
        return matches;
    }

    private <E> Iterable<E> searchSpectra(@NotNull Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
                                          Double scoreThreshold, Double mzTolerance,
                                          Double precursorTolerance, Double molecularWeightTolerance,
                                          boolean searchConsensusSpectra, boolean searchReferenceSpectra,
                                          boolean searchClusterableSpectr, Class<E> classOfE) {

        List<BigInteger> submissionIdList = new ArrayList<>();
        submissionIds.forEach(submissionIdList::add);

        SpectrumQueryBuilderAlt builder = new SpectrumQueryBuilderAlt(submissionIdList,
                searchConsensusSpectra, searchReferenceSpectra, searchClusterableSpectr);
        if (querySpectrum != null)
            builder = builder.withChromatographyType(querySpectrum.getChromatographyType())
                    .withQuerySpectrum(querySpectrum.getPeaks(), mzTolerance, scoreThreshold)
                    .withPrecursor(querySpectrum.getPrecursor(), precursorTolerance)
                    .withMolecularWeight(querySpectrum.getMolecularWeight(), molecularWeightTolerance);

        String query;
        if (classOfE == SpectrumClusterView.class)
            query = builder.buildSpectrumClusterViewQuery();
        else if (classOfE == SpectrumMatch.class)
            query = builder.buildSpectrumMatchQuery();
        else
            throw new IllegalStateException("Unknown class: " + classOfE);

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
                "`Name`, `Precursor`, `RetentionTime`, `Significance`, " +
                "`ClusterId`, `Consensus`, `Reference`, `IntegerMz`, " +
                "`ChromatographyType`, `FileId`, `MolecularWeight`, " +
                "`TopMz1`, `TopMz2`, `TopMz3`, `TopMz4`, `TopMz5`, `TopMz6`, `TopMz7`, `TopMz8`, `TopMz9`, " +
                "`TopMz10`, `TopMz11`, `TopMz12`, `TopMz13`, `TopMz14`, `TopMz15`, `TopMz16`" +
                ") VALUES ");

        for (int i = 0; i < fileList.size(); i++) {
            final List<Spectrum> spectra = fileList.get(i).getSpectra();
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
                        spectrum.getRetentionTime(),
                        spectrum.getSignificance(),
                        spectrum.getCluster() != null ? spectrum.getCluster().getId() : null,
                        spectrum.isConsensus(),
                        spectrum.isReference(),
                        spectrum.isIntegerMz(),
                        String.format("\"%s\"", spectrum.getChromatographyType().name()),
                        savedFileIdList.get(i),
                        spectrum.getMolecularWeight(),
                        //TODO: Expression `y = (x != null) ? x : null` must be equivalent to `y = x`.
                        // Will this work if you just put spectrum.getTopMz1(),... without any conditions?
                        spectrum.getTopMz1() != null ? spectrum.getTopMz1() : null,
                        spectrum.getTopMz2() != null ? spectrum.getTopMz2() : null,
                        spectrum.getTopMz3() != null ? spectrum.getTopMz3() : null,
                        spectrum.getTopMz4() != null ? spectrum.getTopMz4() : null,
                        spectrum.getTopMz5() != null ? spectrum.getTopMz5() : null,
                        spectrum.getTopMz6() != null ? spectrum.getTopMz6() : null,
                        spectrum.getTopMz7() != null ? spectrum.getTopMz7() : null,
                        spectrum.getTopMz8() != null ? spectrum.getTopMz8() : null,
                        spectrum.getTopMz9() != null ? spectrum.getTopMz9() : null,
                        spectrum.getTopMz10() != null ? spectrum.getTopMz10() : null,
                        spectrum.getTopMz11() != null ? spectrum.getTopMz11() : null,
                        spectrum.getTopMz12() != null ? spectrum.getTopMz12() : null,
                        spectrum.getTopMz13() != null ? spectrum.getTopMz13() : null,
                        spectrum.getTopMz14() != null ? spectrum.getTopMz14() : null,
                        spectrum.getTopMz15() != null ? spectrum.getTopMz15() : null,
                        spectrum.getTopMz16() != null ? spectrum.getTopMz16() : null
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

    @Override
    public Iterable<Long> preScreenSpectrum(Spectrum querySpectrum, double mzTolerance){
        PreScreenQueryBuilder preScreenQueryBuilder = new PreScreenQueryBuilder(querySpectrum, mzTolerance);

        final String sqlQuery = preScreenQueryBuilder.build();

        //TODO: The SQL query returns two columns: Id and Common. It's completely wrong to assign it to a list of Long.
        // Can you modify the SQL query to select columns Common and Id (in that order!) and return List<Object[]> from `preScreenSpectrum`
        final Iterable<Long> resultList = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();
        return resultList;
    }
}
