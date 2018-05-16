package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.entities.Spectrum;

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

    @Override
    public Iterable<Hit> findSimilarSpectra(Spectrum querySpectrum, float mzTolerance, float scoreThreshold) {

        querySpectrum = Objects.requireNonNull(querySpectrum, "Query Spectum is Null.");

        String sqlTemplate = "SELECT Hit.SpectrumId, Hit.Score\n" +
                "FROM Spectrum, Submission, (\n:peakMatchSubQuery\n) AS Hit\n" +
                "WHERE Hit.SpectrumId = Spectrum.Id " +
                "AND :precursorConstraint " +
                "AND Spectrum.SubmissionId = Submission.Id " +
                "AND Submission.ChromatographyType = \":queryChromatographyType\"\n" +
                "ORDER BY Hit.Score DESC";

        String peakMatchSubQuery = "\tSELECT SpectrumId, POWER(SUM(Product), 2) AS Score FROM (\n" +
                querySpectrum.getPeaks()
                        .stream()
                        .map(peak -> String.format(
                                "\tSELECT SpectrumId, SQRT(Intensity * %f) AS Product FROM Peak WHERE Mz > %f AND Mz < %f\n",
                                peak.getIntensity(),
                                peak.getMz() - mzTolerance,
                                peak.getMz() + mzTolerance))
                        .collect(Collectors.joining("\tUNION ALL\n")) +
                "\t) AS Result\n" +
                "\tGROUP BY SpectrumId\n" +
                String.format("\tHAVING Score > %f\n", scoreThreshold);

        String sqlQuery = sqlTemplate
                .replace(":peakMatchSubQuery", peakMatchSubQuery)
                .replace(":querySpectrumId", String.valueOf(querySpectrum.getId()))
                .replace(":precursorConstraint",
                        querySpectrum.getPrecursor() == null ? "Spectrum.Precursor IS NULL" : String.format(
                                "Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                                querySpectrum.getPrecursor() - mzTolerance,
                                querySpectrum.getPrecursor() + mzTolerance))
                .replace(":queryChromatographyType",
                        querySpectrum.getSubmission().getChromatographyType().name());

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
