package org.dulab.site.services;

import java.util.ArrayList;
import java.util.List;

class ServiceUtils {

    static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
