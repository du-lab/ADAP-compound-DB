package org.dulab.models.search;

public enum ComparisonOperator implements ComparisonOperatorPredicate {

    EQ {
        @Override
        public String toString(Criterion c) {
            return c.getProperty() + " = " + addQuotes(c.getValue());
        }
    },

    BLOCK {
        @Override
        public String toString(Criterion c) {
            return '(' + c.getValue().toString() + ')';
        }
    };

    private static String addQuotes(Object o) {
        if (o instanceof Number)
            return o.toString();
        else
            return "\"" + o + "\"";
    }
}

interface ComparisonOperatorPredicate {
    String toString(Criterion c);
}


