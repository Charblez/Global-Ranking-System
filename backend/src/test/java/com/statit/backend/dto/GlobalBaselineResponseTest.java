package com.statit.backend.dto;

import com.statit.backend.TestUtils;
import com.statit.backend.model.Category;
import com.statit.backend.model.GlobalBaseline;
import com.statit.backend.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalBaselineResponseTest
{
    @Test
    void fromGlobalBaselineCopiesAllFields()
    {
        User u = new User("u", "u@x", "h", LocalDate.of(2000, 1, 1), null);
        Category category = new Category("c", "d", "u", null, true, u);

        Map<String, String> filters = new HashMap<>();
        filters.put("k", "v");

        GlobalBaseline baseline = new GlobalBaseline(category, filters, 1f, 2f, 3f, 4f, 5f, 6f, 7, "src");
        UUID id = UUID.randomUUID();
        TestUtils.setField(baseline, "baselineId", id);

        GlobalBaselineResponse response = GlobalBaselineResponse.fromGlobalBaseline(baseline);

        assertEquals(id, response.baselineId());
        assertEquals("v", response.filters().get("k"));
        assertEquals(1f, response.mean());
        assertEquals(2f, response.median());
        assertEquals(3f, response.standardDeviation());
        assertEquals(4f, response.lambda());
        assertEquals(5f, response.mu());
        assertEquals(6f, response.sigma());
        assertEquals(7, response.sampleSize());
        assertEquals("src", response.sourceName());
    }
}
