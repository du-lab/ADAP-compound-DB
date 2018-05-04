package org.dulab.site.repositories;

import org.dulab.models.Hit;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.entities.Spectrum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<Hit> searchSpectra(Spectrum querySpectrum, CriteriaBlock criteriaBlock,
                                       float mzTolerance, int numHits, float scoreThreshold) {

        String criteriaBlockString = criteriaBlock.isEmpty()
                ? ""
                : " AND " + criteriaBlock.toString();

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n");
        sqlBuilder.append(querySpectrum.getPeaks()
                .stream()
                .map(p -> "\tSELECT SpectrumId, SQRT(Intensity * "
                    + p.getIntensity()
                    + ") AS Product FROM Peak WHERE Mz > " + (p.getMz() - mzTolerance)
                    + " AND Mz < " + (p.getMz() + mzTolerance)
                    + "\n")  // + criteriaBlockString +
                    .collect(Collectors.joining("\tUNION ALL\n")));
        sqlBuilder.append(") AS Result\n");
        sqlBuilder.append("GROUP BY SpectrumId\n");
        sqlBuilder.append("HAVING Score > :threshold\n"); // POWER(SUM(Product), 2)
        sqlBuilder.append("ORDER BY Score DESC\n");
        sqlBuilder.append("LIMIT :num\n");

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = entityManager
                .createNativeQuery(sqlBuilder.toString(), "SpectrumScoreMapping")
//                .setParameter("eps", mzTolerance)
                .setParameter("num", numHits)
                .setParameter("threshold", scoreThreshold)
                .getResultList();

        List<Hit> hitList = new ArrayList<>(resultList.size());
        for (Object[] columns : resultList) {
            long spectrumId = (long) columns[0];
            double score = (double) columns[1];

//            if (spectrumId == querySpectrum.getId()) continue;

            Hit hit = new Hit();
            hit.setSpectrum(entityManager.find(Spectrum.class, spectrumId));
            hit.setScore(score);
            hitList.add(hit);
        }

        return hitList;
    }
}
