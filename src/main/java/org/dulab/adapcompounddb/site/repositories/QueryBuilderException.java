package org.dulab.adapcompounddb.site.repositories;

import org.springframework.stereotype.Repository;


public class QueryBuilderException extends RuntimeException {

    public QueryBuilderException(String errorMessage) {
        super(errorMessage);
    }
}
