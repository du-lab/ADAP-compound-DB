package org.dulab.models.search;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CriterionTest {

    private Criterion criterionInteger;
    private Criterion criterionDouble;
    private Criterion criterionString;
    private Criterion criterionBlock;

    @Before
    public void setUp() {
        criterionInteger = new Criterion("x", ComparisonOperator.EQ, 5);
        criterionDouble = new Criterion("y", ComparisonOperator.EQ, 1.2345);
        criterionString = new Criterion("text", ComparisonOperator.EQ, "five");
        criterionBlock = new Criterion("", ComparisonOperator.BLOCK, "Block");
    }

    @Test
    public void toStringTest() {
        assertEquals("x = 5", criterionInteger.toString());
        assertEquals("y = 1.2345", criterionDouble.toString());
        assertEquals("text = \"five\"", criterionString.toString());
        assertEquals("(Block)", criterionBlock.toString());
    }
}