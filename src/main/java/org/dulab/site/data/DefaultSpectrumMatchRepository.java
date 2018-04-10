package org.dulab.site.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.site.models.Spectrum;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DefaultSpectrumMatchRepository implements SpectrumMatchRepository {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public List<Spectrum> match(Spectrum querySpectrum) {

        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT SpectrumId, SUM(Score) AS Score FROM (\n");
            builder.append(querySpectrum.getPeaks()
                    .stream()
                    .map(p -> "SpectrumId, Intensity * "
                            + p.getIntensity()
                            + " AS Score FROM Peak WHERE ABS(Peak.mz - "
                            + p.getMz()
                            + ") < 0.1\n")
                    .collect(Collectors.joining("\nUNION")));
            builder.append(") AS Result\n GROUP BY SpectrumId\n ORDER BY Score DESC\n LIMIT 10;");


            List resultList = entityManager
                    .createNativeQuery(builder.toString())
                    .getResultList();

        } catch (NoResultException | NonUniqueResultException e) {
            LOG.warn(e);
            return null;
        } finally {
            entityManager.close();
        }

        return null;
    }
}
