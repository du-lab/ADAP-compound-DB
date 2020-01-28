package org.dulab.adapcompounddb.site.services.utils;

import java.util.ArrayList;
import java.util.List;

public class MappingUtils {

    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
