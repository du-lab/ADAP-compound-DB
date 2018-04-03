package org.dulab.site.authentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.site.data.DBUtil;
import org.dulab.site.data.GenericJpaRepository;
import org.dulab.site.models.UserPrincipal;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

@Repository
public class DefaultUserPrincipalRepository extends GenericJpaRepository<Long, UserPrincipal> implements UserPrincipalRepository {

    private static final Logger LOG = LogManager.getLogger();

    public DefaultUserPrincipalRepository() {
        super(Long.class, UserPrincipal.class);
    }

    @Override
    public UserPrincipal getByUsername(String username) {

        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            return entityManager.createQuery(
                    "SELECT u FROM UserPrincipal u WHERE u.username = :username", UserPrincipal.class)
                    .setParameter("username", username)
                    .getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException e) {
            LOG.warn(e);
            return null;
        }
        finally {
            entityManager.close();
        }
    }
}
