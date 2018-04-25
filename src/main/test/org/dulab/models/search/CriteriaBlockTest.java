package org.dulab.models.search;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CriteriaBlockTest {

    private CriteriaBlock blockAnd;
    private CriteriaBlock blockOr;

    @Before
    public void setUp() throws Exception {

        Criterion criterion1 = mock(Criterion.class);
        when(criterion1.toString()).thenReturn("Criterion1");

        Criterion criterion2 = mock(Criterion.class);
        when(criterion2.toString()).thenReturn("Criterion2");

        List<Criterion> criteria = Arrays.asList(criterion1, criterion2);

        blockAnd = new CriteriaBlock(SetOperator.AND, criteria);
        blockOr = new CriteriaBlock(SetOperator.OR, criteria);
    }

    @Test
    public void toStringTest() {
        assertEquals("Criterion1 AND Criterion2", blockAnd.toString());
        assertEquals("Criterion1 OR Criterion2", blockOr.toString());
    }
}