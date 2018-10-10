package org.dulab.adapcompounddb.site.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
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
        final
        List<Object[]> resultList = entityManager
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

    public void savePeaksFromSpectrum(final SubmissionServiceImpl submissionServiceImpl, final List<Spectrum> spectrumList, final List<Spectrum> savedSpectrumList) {
        final StringBuilder sql = new StringBuilder("INSERT INTO `peak`(" +
                "`Mz`, `Intensity`, `SpectrumId`) VALUES ");

        for(int i=0; i<spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();
            for(int j=0; j<peaks.size(); j++) {
                if(i != 0 || j != 0) {
                    sql.append(",");
                }
                final Peak p = peaks.get(j);
                sql.append("(");
                sql.append(p.getMz());
                sql.append(",");
                sql.append(p.getIntensity());
                sql.append(",");
                sql.append(savedSpectrumList.get(i).getId());
                sql.append(")");
            }
        }
        final javax.persistence.Query query = entityManager.createNativeQuery(sql.toString());
        query.executeUpdate();
    }
}
