package org.dulab.adapcompounddb.site.repositories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    private static final String PEAK_VALUE_SQL_STRING = "(%f,%f,%d)";
    private static final String PROPERTY_VALUE_SQL_STRING = "(%d, %s, %s)";
    private static final String SPECTRUM_VALUE_SQL_STRING = "(%s, %f, %f, %f, %d, %b, %b, %b, %s, %d)";

    public static final String DOUBLE_QUOTE = "\"";
    public static final String COMMA = ",";
    @PersistenceContext
    private EntityManager entityManager;

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

        @SuppressWarnings("unchecked") final List<Object[]> resultList = entityManager
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
    public Iterable<SpectrumClusterView> searchConsensusSpectra(
            Spectrum querySpectrum, double scoreThreshold, double mzTolerance,
            String species, String source, String disease) {

        String query = "SELECT SpectrumCluster.Id, ConsensusSpectrum.Name, COUNT(DISTINCT File.SubmissionId) AS Size, Score, ";
        query += "AVG(Spectrum.Significance) AS AverageSignificance, MIN(Spectrum.Significance) AS MinimumSignificance, ";
        query += "MAX(Spectrum.Significance) AS MaximumSignificance, ConsensusSpectrum.ChromatographyType FROM (\n";
        query += "SELECT ClusterId, POWER(SUM(Product), 2) AS Score FROM (\n";
        query += querySpectrum.getPeaks().stream()
                .map(p -> String.format("\tSELECT ClusterId, SQRT(Intensity * %f) AS Product " +
                                "FROM Peak INNER JOIN Spectrum ON Peak.SpectrumId = Spectrum.Id " +  //
                                "WHERE Spectrum.Consensus IS TRUE AND Peak.Mz > %f AND Peak.Mz < %f\n",
                        p.getIntensity(), p.getMz() - mzTolerance, p.getMz() + mzTolerance))
                .collect(Collectors.joining("\tUNION ALL\n"));
        query += ") AS SearchTable ";
        query += "GROUP BY ClusterId HAVING Score > :scoreThreshold\n";
        query += ") AS ScoreTable JOIN SpectrumCluster ON SpectrumCluster.Id = ClusterId\n";
        query += "JOIN Spectrum AS ConsensusSpectrum ON ConsensusSpectrum.Id = SpectrumCluster.ConsensusSpectrumId\n";
        query += "JOIN Spectrum ON Spectrum.ClusterId = SpectrumCluster.Id\n";
        query += "JOIN File ON File.Id = Spectrum.FileId\n";
        query += "WHERE File.SubmissionId IN (SELECT DISTINCT * FROM (SELECT SubmissionId FROM SubmissionTag " +
                "WHERE :species IS NULL OR :species='all' OR (TagKey='species (common)' AND TagValue=:species)) AS SpeciesTag " +
                "INNER JOIN (SELECT SubmissionId FROM SubmissionTag " +
                "WHERE :source IS NULL OR :source='all' OR (TagKey='sample source' AND TagValue=:source)) AS SourceTag " +
                "USING (SubmissionId) " +
                "INNER JOIN (SELECT SubmissionId FROM SubmissionTag " +
                "WHERE :disease IS NULL OR :disease='all' OR (TagKey='disease' AND TagValue=:disease)) AS DiseaseTag " +
                "USING (SubmissionId))\n";
        query += "GROUP BY Spectrum.ClusterId ORDER BY Score DESC";

        @SuppressWarnings("unchecked")
        List<SpectrumClusterView> resultList = entityManager
                .createNativeQuery(query, SpectrumClusterView.class)
                .setParameter("scoreThreshold", scoreThreshold)
                .setParameter("species", species)
                .setParameter("source", source)
                .setParameter("disease", disease)
                .getResultList();

        return resultList;
    }

    @Override
    public void savePeaksAndPropertiesQuery(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {
        final StringBuilder peakSql = new StringBuilder("INSERT INTO `Peak`(" +
                "`Mz`, `Intensity`, `SpectrumId`) VALUES ");
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `SpectrumProperty`(" +
                "`SpectrumId`, `Name`, `Value`) VALUES ");

        final String peakValueString = "(%f, %f, %d)";
        final String propertyValueString = "(%d, %s, %s)";

        for (int i = 0; i < spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();
            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();

            for (int j = 0; j < peaks.size(); j++) {
                if (i != 0 || j != 0) {
                    peakSql.append(COMMA);
                }
                final Peak p = peaks.get(j);

                peakSql.append(String.format(peakValueString, p.getMz(), p.getIntensity(), savedSpectrumIdList.get(i)));
            }

            for (int j = 0; j < properties.size(); j++) {
                if (i != 0 || j != 0) {
                    propertySql.append(COMMA);
                }
                final SpectrumProperty sp = properties.get(j);
                propertySql.append(String.format(propertyValueString, savedSpectrumIdList.get(i), DOUBLE_QUOTE + sp.getName() + DOUBLE_QUOTE, DOUBLE_QUOTE + sp.getValue() + DOUBLE_QUOTE));
            }
        }

        final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
        peakQuery.executeUpdate();
        final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
        propertyQuery.executeUpdate();
    }

    @Override
    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList) {
        final List<Spectrum> spectrumList = new ArrayList<>();

        final StringBuilder insertSql = new StringBuilder("INSERT INTO `Spectrum`(" +
                "`Name`, `Precursor`, `RetentionTime`, `Significance`, " +
                "`ClusterId`, `Consensus`, `Reference`, `IntegerMz`, " +
                "`ChromatographyType`, `FileId`" +
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
                        DOUBLE_QUOTE + spectrum.getName() + DOUBLE_QUOTE,
                        spectrum.getPrecursor(),
                        spectrum.getRetentionTime(),
                        spectrum.getSignificance(),
                        spectrum.getCluster() != null ? spectrum.getCluster().getId() : null,
                        spectrum.isConsensus(),
                        spectrum.isReference(),
                        spectrum.isIntegerMz(),
                        DOUBLE_QUOTE + spectrum.getChromatographyType().name() + DOUBLE_QUOTE,
                        savedFileIdList.get(i)
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
}
