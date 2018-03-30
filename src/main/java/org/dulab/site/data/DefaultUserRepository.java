package org.dulab.site.data;

import org.dulab.site.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DefaultUserRepository implements UserRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Iterable<User> getAll() {
        return this.entityManager
                .createQuery("SELECT u FROM User u ORDER BY u.lastName", User.class)
                .getResultList();
    }

    @Override
    public User get(long id) {
        return this.entityManager
                .createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public void add(User user) {
        this.entityManager.persist(user);
    }

    @Override
    public void update(User user) {
        this.entityManager.merge(user);
    }

    @Override
    public void delete(User user) {
        this.entityManager.remove(user);
    }

    @Override
    public void delete(long id) {
        this.entityManager
                .createQuery("DELETE FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
