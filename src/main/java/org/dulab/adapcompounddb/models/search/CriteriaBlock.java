package org.dulab.adapcompounddb.models.search;

import java.util.ArrayList;
import java.util.Collection;

public class CriteriaBlock extends ArrayList<Criterion> {

    private final SetOperator operator;

    public CriteriaBlock(SetOperator operator) {
        super();
        this.operator = operator;
    }

    public CriteriaBlock(SetOperator operator, int initialCapacity) {
        super(initialCapacity);
        this.operator = operator;
    }

    public CriteriaBlock(SetOperator operator, Collection<? extends Criterion> criteria) {
        super(criteria);
        this.operator = operator;
    }

    @Override
    public String toString() {
        return this.operator.toString(this);
    }
}
