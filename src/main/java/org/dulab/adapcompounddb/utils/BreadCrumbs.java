package org.dulab.adapcompounddb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BreadCrumbs {
    // change to BreadCrumb
    private String label;
    private String url;

}
