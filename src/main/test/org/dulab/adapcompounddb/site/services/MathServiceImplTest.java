package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MathServiceImplTest {

    private MathService mathService;

    @Mock
    SubmissionCategory category1, category2;

    @Before
    public void setUp() {
        mathService = new MathServiceImpl();
    }

    @Test
    public void diversityIndexTest() {

        // Diversity of a 50/50 list
        double diversity = mathService.diversityIndex(
                Arrays.asList(category1, category1, category2, category2));
        assertEquals(2.0, diversity, 1e-12);

        // Diversity of a list with the same category
        assertEquals(1.0, mathService.diversityIndex(Arrays.asList(category1, category1)),1e-12);

        // Diversity of an empty list
        assertEquals(1.0, mathService.diversityIndex(new ArrayList<>(0)),1e-12);
    }
}