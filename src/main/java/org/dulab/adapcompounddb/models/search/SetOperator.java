package org.dulab.adapcompounddb.models.search;

import java.util.Collection;
import java.util.stream.Collectors;

public enum SetOperator implements SetOperatorPredicate {

    AND {
        @Override
        public String toString(Collection<Criterion> collection) {
            return collection.stream()
                    .map(Criterion::toString)
                    .collect(Collectors.joining(" AND "));
        }
    },

    OR {
        @Override
        public String toString(Collection<Criterion> collection) {
            return collection.stream()
                    .map(Criterion::toString)
                    .collect(Collectors.joining(" OR "));
        }
    }
}

interface SetOperatorPredicate {
    String toString(Collection<Criterion> collection);
}
