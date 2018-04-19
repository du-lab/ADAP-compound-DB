package org.dulab.site.repositories;

import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.models.UserParameters;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustomization {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<Hit> searchSpectra(Spectrum querySpectrum, UserParameters parameters) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n");
        sqlBuilder.append(querySpectrum.getPeaks()
                .stream()
                .map(p -> "\tSELECT SpectrumId, SQRT(Intensity * "
                    + p.getIntensity()
                    + ") AS Product FROM Peak WHERE ABS(Mz - "
                    + p.getMz()
                    + ") < :eps\n")
                    .collect(Collectors.joining("\tUNION\n")));
        sqlBuilder.append(") AS Result\n");
        sqlBuilder.append("GROUP BY SpectrumId\n");
        sqlBuilder.append("HAVING Score > :threshold\n"); // POWER(SUM(Product), 2)
        sqlBuilder.append("ORDER BY Score DESC\n");
        sqlBuilder.append("LIMIT :num\n");

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = entityManager
                .createNativeQuery(sqlBuilder.toString(), "SpectrumScoreMapping")
                .setParameter("eps", parameters.getSpectrumSearchMzTolerance())
                .setParameter("num", parameters.getSpectrumSearchNumHits())
                .setParameter("threshold", parameters.getSpectrumSearchScoreThreshold())
                .getResultList();

        List<Hit> hitList = new ArrayList<>(resultList.size());
        for (Object[] columns : resultList) {
            Hit hit = new Hit();
            hit.setSpectrum(entityManager.find(Spectrum.class, columns[0]));
            hit.setScore((Double) columns[1]);
            hitList.add(hit);
        }

        return hitList;
    }
}
