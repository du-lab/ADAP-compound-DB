package org.dulab.site.repositories;

import org.dulab.site.data.DBUtil;
import org.dulab.site.data.GenericJpaRepository;
import org.dulab.models.Submission;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class SubmissionRepositoryImpl extends GenericJpaRepository <Long, Submission>
        implements SubmissionRepository {

    public SubmissionRepositoryImpl() {
        super(Long.class, Submission.class);
    }

    @Override
    public List<Submission> getSubmissionsByUserId(long userId) {

        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
//            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//            CriteriaQuery<Submission> query = builder.createQuery(Submission.class);
//
//            return entityManager.createQuery(
//                    query.where(
//                            builder.equal(
//                                    query.from(Submission.class).get("UserPrincipalId"),
//                                    id)))
//                    .getResultList();
            return entityManager.createQuery(
                    "select s from Submission s where s.user.id = :userId", Submission.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
        finally {
            entityManager.close();
        }
    }
}
