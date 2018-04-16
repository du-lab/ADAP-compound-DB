package org.dulab.site.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.site.data.DBUtil;
import org.dulab.site.data.GenericJpaRepository;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DefaultSpectrumRepository extends GenericJpaRepository<Long, Spectrum> implements SpectrumRepository {

    private static final Logger LOG = LogManager.getLogger();

    public DefaultSpectrumRepository() {
        super(Long.class, Spectrum.class);
    }

    @Override
    public List<Hit> match(Spectrum querySpectrum) {

        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT SpectrumId, SUM(Score) AS Score FROM (\n");
            sqlBuilder.append(querySpectrum.getPeaks()
                    .stream()
                    .map(p -> "SELECT SpectrumId, Intensity * "
                            + p.getIntensity()
                            + " AS Score FROM Peak WHERE ABS(Peak.mz - "
                            + p.getMz()
                            + ") < 0.1\n")
                    .collect(Collectors.joining("\nUNION\n")));
            sqlBuilder.append(") AS Result\n GROUP BY SpectrumId\n ORDER BY Score DESC\n LIMIT 10;");

            List<Object[]> resultList = entityManager
                    .createNativeQuery(sqlBuilder.toString())
                    .getResultList();

            List<Hit> hitList = new ArrayList<>();
            for (Object[] objects : resultList) {

                Hit hit = new Hit();
                hit.setSpectrum(get(Long.valueOf(objects[0].toString())));
                hit.setScore((Double) objects[1]);
                hitList.add(hit);
            }

            System.out.println();

        } catch (NoResultException | NonUniqueResultException e) {
            LOG.warn(e);
            return null;
        } finally {
            entityManager.close();
        }

        return null;
    }
}
