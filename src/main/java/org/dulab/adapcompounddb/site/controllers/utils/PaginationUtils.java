package org.dulab.adapcompounddb.site.controllers.utils;

import org.dulab.adapcompounddb.models.dto.ClusterDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationUtils {

    public static List<ClusterDTO> getPage(List<ClusterDTO> clusters, int start, int length, int column,
                                           String sortDirection) {



        return IntStream.range(start, Math.min(start + length, clusters.size()))
                .mapToObj(clusters::get)
                .collect(Collectors.toList());
    }
}
