package org.dulab.site.data;

import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated
public interface GenericRepository<I extends Serializable, E extends Serializable> {

    @NotNull
    Iterable<E> getAll();

    E get(@NotNull I id);

    void add(@NotNull E entry);

    void update(@NotNull E entry);

    void delete(@NotNull E entry);

    void deleteById(@NotNull I id);
}
