package org.dulab.adapcompounddb.site.data;

import java.io.Serializable;

public abstract class GenericBaseRepository<I extends Serializable, E extends Serializable>
        implements GenericRepository<I, E> {

    protected final Class<I> idClass;
    protected final Class<E> entityClass;

    public GenericBaseRepository(Class<I> idClass, Class<E> entityClass) {
        this.idClass = idClass;
        this.entityClass = entityClass;
    }
}
