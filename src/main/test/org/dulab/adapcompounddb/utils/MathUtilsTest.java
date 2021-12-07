package org.dulab.adapcompounddb.utils;

import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MathUtilsTest {

    @Mock
    SubmissionCategory category1, category2;

    @Test
    public void diversityIndexTest() {

        // Diversity of a 50/50 list
        double diversity = MathUtils.diversityIndex(
                Arrays.asList(category1, category1, category2, category2));
        assertEquals(2.0, diversity, 1e-12);

        // Diversity of a list with the same category
        assertEquals(1.0, MathUtils.diversityIndex(Arrays.asList(category1, category1)),1e-12);

        // Diversity of an empty list
        assertEquals(1.0, MathUtils.diversityIndex(new ArrayList<>(0)),1e-12);

        // Diversity of a list with the null entry
        assertEquals(2.0, MathUtils.diversityIndex(Arrays.asList(null, category1)),1e-12);
    }
}