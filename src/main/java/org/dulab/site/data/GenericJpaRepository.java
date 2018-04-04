package org.dulab.site.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;

public abstract class GenericJpaRepository<I extends Serializable, E extends Serializable>
        extends GenericBaseRepository<I, E> {

    private static final Logger LOG = LogManager.getLogger();

    public GenericJpaRepository(Class<I> idClass, Class<E> entityClass) {
        super(idClass, entityClass);
    }

    @Override
    public Iterable<E> getAll() {
        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<E> query = builder.createQuery(entityClass);
            return entityManager
                    .createQuery(
                            query.select(
                                    query.from(entityClass)))
                    .getResultList();
        }
        finally {
            entityManager.close();
        }
    }

    @Override
    public E get(I id) {
        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            return entityManager.find(entityClass, id);
        }
        finally {
            entityManager.close();
        }
    }

    @Override
    public void add(E entity) throws ConstraintViolationException {

        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.persist(entity);
            entityTransaction.commit();
        }
        catch (Exception e) {
            entityTransaction.rollback();
            throw e;
        }
        finally {
            entityManager.close();
        }
    }

    @Override
    public void update(E entity) {
        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            entityManager.merge(entity);
        }
        finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(E entity) {
        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            entityManager.remove(entity);
        }
        finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteById(I id) {
        EntityManager entityManager = DBUtil.getEmFactory().createEntityManager();
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaDelete<E> query = builder.createCriteriaDelete(entityClass);
            entityManager.createQuery(
                    query.where(
                            builder.equal(
                                    query.from(entityClass).get("id"), id)))
                    .executeUpdate();
        }
        finally {
            entityManager.close();
        }
    }
}
