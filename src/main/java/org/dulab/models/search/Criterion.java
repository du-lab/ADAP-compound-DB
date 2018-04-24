package org.dulab.models.search;

public class Criterion {

    private final String property;
    private final ComparisonOperator operator;
    private final Object value;

    public Criterion(String property, ComparisonOperator operator, Object value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return operator.toString(this);
    }
}
