package org.dulab.adapcompounddb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.modelmapper.ModelMapper;

public class ObjectMapperUtils {

    ModelMapper mapper;

    public ObjectMapperUtils() {
        super();
        mapper = new ModelMapper();
    }

    public <T> T map(final Object source, final Class<T> destination) {
        return mapper.map(source, destination);
    }

    public <T, E> List<E> map(final List<T> source, final Class<E> destination) {
        if (source == null) {
            return Collections.emptyList();
        }
        final List<E> destinationList = new ArrayList<>();
        final Iterator<T> itr = source.iterator();
        while (itr.hasNext()) {
            destinationList.add(map(itr.next(), destination));
        }

        return destinationList;
    }
}
