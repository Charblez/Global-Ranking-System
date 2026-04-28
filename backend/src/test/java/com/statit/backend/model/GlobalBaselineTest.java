package com.statit.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalBaselineTest
{
    private Category newCategory()
    {
        User u = new User("u", "u@x.com", "h", LocalDate.of(2000, 1, 1), null);
        return new Category("c", "d", "units", null, true, u);
    }

    @Test
    void defaultConstructorLeavesFieldsNull()
    {
        GlobalBaseline baseline = new GlobalBaseline();
        assertNull(baseline.getCategory());
        assertNull(baseline.getMean());
    }

    @Test
    void categoryOnlyConstructorInitializesEmptyFilters()
    {
        Category c = newCategory();
        GlobalBaseline baseline = new GlobalBaseline(c);
        assertSame(c, baseline.getCategory());
        assertNotNull(baseline.getFilters());
        assertTrue(baseline.getFilters().isEmpty());
    }

    @Test
    void fullConstructorPopulatesAllFields()
    {
        Category c = newCategory();
        Map<String, String> filters = new HashMap<>();
        filters.put("country", "US");

        GlobalBaseline baseline = new GlobalBaseline(c, filters, 5f, 4f, 1.5f, 0.1f, 0.2f, 0.3f, 10, "src");

        assertSame(c, baseline.getCategory());
        assertEquals("US", baseline.getFilters().get("country"));
        assertEquals(5f, baseline.getMean());
        assertEquals(4f, baseline.getMedian());
        assertEquals(1.5f, baseline.getStandardDeviation());
        assertEquals(0.1f, baseline.getLambda());
        assertEquals(0.2f, baseline.getMu());
        assertEquals(0.3f, baseline.getSigma());
        assertEquals(10, baseline.getSampleSize());
        assertEquals("src", baseline.getSourceName());
    }

    @Test
    void fullConstructorWithNullFiltersCreatesEmptyMap()
    {
        GlobalBaseline baseline = new GlobalBaseline(newCategory(), null, 0f, 0f, 0f, 0f, 0f, 0f, 0, "s");
        assertNotNull(baseline.getFilters());
        assertTrue(baseline.getFilters().isEmpty());
    }

    @Test
    void updateReplacesAllNonIdFields()
    {
        GlobalBaseline baseline = new GlobalBaseline(newCategory());
        Map<String, String> filters = new HashMap<>();
        filters.put("k", "v");

        baseline.update(filters, 1f, 2f, 3f, 4f, 5f, 6f, 7, "src");

        assertEquals("v", baseline.getFilters().get("k"));
        assertEquals(1f, baseline.getMean());
        assertEquals(2f, baseline.getMedian());
        assertEquals(3f, baseline.getStandardDeviation());
        assertEquals(4f, baseline.getLambda());
        assertEquals(5f, baseline.getMu());
        assertEquals(6f, baseline.getSigma());
        assertEquals(7, baseline.getSampleSize());
        assertEquals("src", baseline.getSourceName());
    }

    @Test
    void updateWithNullFiltersCreatesEmptyMap()
    {
        GlobalBaseline baseline = new GlobalBaseline(newCategory());
        baseline.update(null, 1f, 2f, 3f, 4f, 5f, 6f, 7, "s");
        assertNotNull(baseline.getFilters());
        assertTrue(baseline.getFilters().isEmpty());
    }

    @Test
    void settersUpdateFields()
    {
        GlobalBaseline baseline = new GlobalBaseline(newCategory());
        Map<String, String> filters = new HashMap<>();
        filters.put("a", "b");
        baseline.setFilters(filters);
        baseline.setMean(1f);
        baseline.setMedian(2f);
        baseline.setStandardDeviation(3f);
        baseline.setLambda(4f);
        baseline.setMu(5f);
        baseline.setSigma(6f);
        baseline.setSampleSize(7);
        baseline.setSourceName("src");

        assertEquals("b", baseline.getFilters().get("a"));
        assertEquals(1f, baseline.getMean());
        assertEquals(2f, baseline.getMedian());
        assertEquals(3f, baseline.getStandardDeviation());
        assertEquals(4f, baseline.getLambda());
        assertEquals(5f, baseline.getMu());
        assertEquals(6f, baseline.getSigma());
        assertEquals(7, baseline.getSampleSize());
        assertEquals("src", baseline.getSourceName());
    }
}
