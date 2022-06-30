package org.dulab.adapcompounddb.site.services.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class DataUtils {

    /**
     * Create an instance of Pageable for given parameters
     * @param start start of the page
     * @param length length of the page
     * @param column column name for sorting
     * @param direction direction of sorting
     * @return an instance of Pageable
     */
    public static Pageable createPageable(int start, int length, String column, String direction) {
        return PageRequest.of(start / length, length, (column != null)
//                ? new Sort(Sort.Direction.fromString(direction), column)
                ? Sort.by(Sort.Direction.fromString(direction), column)
                : Sort.unsorted());
    }
}
