package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params) {

        SpectrumQueryBuilder queryBuilder = new SpectrumQueryBuilder(
                searchType, querySpectrum.getChromatographyType(), params.getExcludeSpectra());

        if (params.getScoreThreshold() != null && params.getMzTolerance() != null)
            queryBuilder.setSpectrum(querySpectrum, params.getMzTolerance(), params.getScoreThreshold());

        if (querySpectrum.getPrecursor() != null && params.getPrecursorTolerance() != null)
            queryBuilder.setPrecursorRange(querySpectrum.getPrecursor(), params.getPrecursorTolerance());

        if (querySpectrum.getRetentionTime() != null && params.getRetTimeTolerance() != null)
            queryBuilder.setRetentionTimeRange(querySpectrum.getRetentionTime(), params.getRetTimeTolerance());

        queryBuilder.setTags(params.getTags());

//        String sqlQueries = queryBuilder.build();
//        entityManager.createNativeQuery(sqlQueries[0]).executeUpdate();
        String sqlQuery = queryBuilder.build();

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery, "SpectrumScoreMapping")
                .getResultList();

//        entityManager.createNativeQuery(sqlQueries[2]).executeUpdate();

        List<SpectrumMatch> matches = new ArrayList<>();
        for (Object[] objects : resultList) {
            long matchSpectrumId = (long) objects[0];
            double score = (double) objects[1];

            SpectrumMatch match = new SpectrumMatch();
            match.setQuerySpectrum(querySpectrum);
            match.setMatchSpectrum(entityManager.find(Spectrum.class, matchSpectrumId));
            match.setScore(score);
            matches.add(match);
        }

        return matches;
    }
}
