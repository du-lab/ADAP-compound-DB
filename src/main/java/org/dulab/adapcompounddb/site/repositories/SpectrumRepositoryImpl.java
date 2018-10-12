package org.dulab.adapcompounddb.site.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.SubmissionServiceImpl;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

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

        //        String sqlQueries = queryBuilder.build();
        //        entityManager.createNativeQuery(sqlQueries[0]).executeUpdate();
        final String sqlQuery = queryBuilder.build();

        @SuppressWarnings("unchecked")
        final List<Object[]> resultList = entityManager
        .createNativeQuery(sqlQuery, "SpectrumScoreMapping")
        .getResultList();

        //        entityManager.createNativeQuery(sqlQueries[2]).executeUpdate();

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
    public void savePeaksAndPropertiesQuery(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {
        final StringBuilder peakSql = new StringBuilder("INSERT INTO `peak`(" +
                "`Mz`, `Intensity`, `SpectrumId`) VALUES ");
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `spectrumproperty`(" +
                "`SpectrumId`, `Name`, `Value`) VALUES ");

        for(int i=0; i<spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();
            for(int j=0; j<peaks.size(); j++) {
                if(i != 0 || j != 0) {
                    peakSql.append(",");
                }
                final Peak p = peaks.get(j);
                peakSql.append("(");
                peakSql.append("\"");
                peakSql.append(p.getMz());
                peakSql.append("\"");
                peakSql.append(",");
                peakSql.append(p.getIntensity());
                peakSql.append(",");
                peakSql.append(savedSpectrumIdList.get(i));
                peakSql.append(")");
            }

            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();
            for(int j=0; j<properties.size(); j++) {
                if(i != 0 || j != 0) {
                    propertySql.append(",");
                }
                final SpectrumProperty sp = properties.get(j);
                propertySql.append("(");
                propertySql.append(savedSpectrumIdList.get(i));
                propertySql.append(",");
                propertySql.append("\"");
                propertySql.append(sp.getName());
                propertySql.append("\"");
                propertySql.append(",");
                propertySql.append("\"");
                propertySql.append(sp.getValue());
                propertySql.append("\"");
                propertySql.append(")");
            }
        }
//        peakSql.append(";");
//        peakSql.append(propertySql);
        final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
        peakQuery.executeUpdate();
        final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
        propertyQuery.executeUpdate();
    }

    /*@Override
    public void savePropertiesFromSpectrum(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `spectrumproperty`(" +
                "`SpectrumId`, `Name`, `Value`) VALUES ");

        for(int i=0; i<spectrumList.size(); i++) {
            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();
            for(int j=0; j<properties.size(); j++) {
                if(i != 0 || j != 0) {
                    propertySql.append(",");
                }
                final SpectrumProperty sp = properties.get(j);
                propertySql.append("(");
                propertySql.append(savedSpectrumIdList.get(i));
                propertySql.append(",");
                propertySql.append(sp.getName());
                propertySql.append(",");
                propertySql.append(sp.getValue());
                propertySql.append(")");
            }
        }
        final Query query = entityManager.createNativeQuery(propertySql.toString());
        query.executeUpdate();
    }*/

    @Override
    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList) {
        final List<Spectrum> spectrumList = new ArrayList<>();

        final StringBuilder insertSql = new StringBuilder("INSERT INTO `spectrum`(" +
                "`Name`, `Precursor`, `RetentionTime`," +
                "`Significance`, `ChromatographyType`, `FileId`" +
                ") VALUES ");

        for(int i=0; i<fileList.size(); i++) {
            final List<Spectrum> spectra = fileList.get(i).getSpectra();
            spectrumList.addAll(spectra);
            for(int j=0; j<spectra.size(); j++) {
                if(i != 0 || j != 0) {
                    insertSql.append(",");
                }
                final Spectrum spectrum = spectra.get(j);
                insertSql.append("(\"");
                insertSql.append(spectrum.getName());
                insertSql.append("\"");
                insertSql.append(",");

                if(spectrum.getPrecursor() != null) {
                    insertSql.append("\"");
                }
                insertSql.append(spectrum.getPrecursor());
                if(spectrum.getPrecursor() != null) {
                    insertSql.append("\"");
                }

                insertSql.append(",");

                if(spectrum.getRetentionTime() != null) {
                    insertSql.append("\"");
                }
                insertSql.append(spectrum.getRetentionTime());
                if(spectrum.getRetentionTime() != null) {
                    insertSql.append("\"");
                }

                insertSql.append(",");

                if(spectrum.getSignificance() != null) {
                    insertSql.append("\"");
                }
                insertSql.append(spectrum.getSignificance());
                if(spectrum.getSignificance() != null) {
                    insertSql.append("\"");
                }

                insertSql.append(",");

                insertSql.append("\"");
                insertSql.append(spectrum.getChromatographyType().name());
                insertSql.append("\"");

                insertSql.append(",");

                insertSql.append("\"");
                insertSql.append(savedFileIdList.get(i));
                insertSql.append("\")");
            }
        }
        final Query insertQuery = entityManager.createNativeQuery(insertSql.toString());
        insertQuery.executeUpdate();

        List<Long> fileIds = new ArrayList<>();
        fileList.stream().forEach(file -> fileIds.add(file.getId()));
        final StringBuilder selectSql = new StringBuilder("select s.id from Spectrum s where s.file.id in (:fileIds)");

        TypedQuery selectQuery = entityManager.createQuery(selectSql.toString(), Long.class);
        selectQuery.setParameter("fileIds", fileIds);

        List<Long> spectrumIds = selectQuery.getResultList();

        savePeaksAndPropertiesQuery(spectrumList, spectrumIds);
    }
}
