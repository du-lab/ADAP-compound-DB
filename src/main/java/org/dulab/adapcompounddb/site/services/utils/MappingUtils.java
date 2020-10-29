package org.dulab.adapcompounddb.site.services.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MappingUtils {

    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public static <E> List<E> randomSubList(List<E> list, int n) {
        Collections.shuffle(list, new Random(0));
        return list.size() > n ? list.subList(0, n) : list;
    }
}
