package org.dulab.site.repositories;

import org.dulab.models.Hit;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.entities.Spectrum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public Iterable<Hit> findSimilarSpectra(Spectrum querySpectrum,
                                            float mzTolerance, float scoreThreshold) {

        querySpectrum = Objects.requireNonNull(querySpectrum, "Query Spectum is Null.");

        String peakMatchSubQuery = "SELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n" +
                querySpectrum.getPeaks()
                .stream()
                .map(peak -> String.format(
                        "SELECT SpectrumId, SQRT(Intensity * %f) AS Product FROM Peak WHERE Mz > %f AND Mz < %f\n",
                        peak.getIntensity(),
                        peak.getMz() - mzTolerance,
                        peak.getMz() + mzTolerance))
                .collect(Collectors.joining("UNION ALL\n")) +
                ") AS Match GROUP BY SpectrumId\n" +
                String.format("HAVING Score > %f\n", scoreThreshold);

        String precursorConstraint = querySpectrum.getPrecursor() == null ? "" : String.format(
                "AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                querySpectrum.getPrecursor() - mzTolerance,
                querySpectrum.getPrecursor() + mzTolerance);

        String sqlQuery = "SELECT Hit.SpectrumId, Hit.Score\n" +
                String.format("FROM Spectrum, Submission, (%s) AS Hit\n", peakMatchSubQuery) +
                "WHERE Hit.SpectrumId = Spectrum.Id\n" +
                String.format("AND Hit.SpectrumId != %d\n", querySpectrum.getId()) +
                "AND Spectrum.SubmissionId = Submission.Id\n" +
                String.format(
                        "AND Submission.ChromatographyType = %s\n",
                        querySpectrum.getSubmission().getChromatographyType()) +
                precursorConstraint +
                "ORDER BY Hit.Score DESC";

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery, "SpectrumScoreMapping")
                .getResultList();

        List<Hit> hitList = new ArrayList<>(resultList.size());
        for (Object[] columns : resultList) {
            long spectrumId = (long) columns[0];
            double score = (double) columns[1];

            Hit hit = new Hit();
            hit.setSpectrum(entityManager.find(Spectrum.class, spectrumId));
            hit.setScore(score);
            hitList.add(hit);
        }

        return hitList;
    }
}
