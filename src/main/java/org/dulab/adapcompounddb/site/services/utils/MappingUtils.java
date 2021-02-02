package org.dulab.adapcompounddb.site.services.utils;

import java.util.*;

public class MappingUtils {

    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public static <E> Map<Long, E> toMap(Iterable<Object[]> iterable) {
        Map<Long, E> map = new HashMap<>();
        iterable.forEach(it -> map.put((Long) it[0], (E) it[1]));
        return map;
    }

    public static <E> List<E> randomSubList(List<E> list, int n) {
        Collections.shuffle(list, new Random(0));
        return list.size() > n ? list.subList(0, n) : list;
    }
}
