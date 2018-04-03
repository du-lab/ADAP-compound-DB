package org.dulab.site.authentication;

import org.dulab.site.data.DBUtil;
import org.dulab.site.data.GenericJpaRepository;
import org.dulab.site.models.UserPrincipal;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class DefaultUserPrincipalRepository extends GenericJpaRepository<Long, UserPrincipal> implements UserPrincipalRepository {

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
        finally {
            entityManager.close();
        }
    }
}
