package org.dulab.adapcompounddb.site.controllers.utils;

import org.dulab.adapcompounddb.models.dto.SearchResultDTO;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationUtils {

    public static List<SearchResultDTO> getPage(List<SearchResultDTO> clusters, int start, int length, int column,
                                                String sortDirection) {

        // Sort the list based on the column

        Function<SearchResultDTO, Comparable> field = SearchResultDTO.COLUMN_TO_FIELD_MAP.get(column);

        if (field == null)
            throw new IllegalStateException("Cannot find field for column " + column);

        Comparator<SearchResultDTO> comparator = sortDirection.equalsIgnoreCase("desc")
                ? Comparator.comparing(field, Comparator.nullsLast(Comparator.reverseOrder()))
                : Comparator.comparing(field, Comparator.nullsLast(Comparator.naturalOrder()));

        clusters.sort(comparator);

        return IntStream.range(start, Math.min(start + length, clusters.size()))
                .mapToObj(clusters::get)
                .collect(Collectors.toList());
    }
}
