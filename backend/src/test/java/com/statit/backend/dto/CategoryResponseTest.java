package com.statit.backend.dto;

import com.statit.backend.TestUtils;
import com.statit.backend.model.Category;
import com.statit.backend.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryResponseTest
{
    @Test
    void fromCategoryCopiesAllFields()
    {
        User founder = new User("f", "f@x", "h", LocalDate.of(2000, 1, 1), null);
        Category category = new Category("Run", "100m", "s", Arrays.asList("sport"), true, founder);

        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 0, 0);
        TestUtils.setField(category, "categoryId", id);
        TestUtils.setField(category, "createdAt", createdAt);

        CategoryResponse response = CategoryResponse.fromCategory(category, "ok");

        assertEquals(id, response.categoryId());
        assertEquals("Run", response.name());
        assertEquals("100m", response.description());
        assertEquals("s", response.units());
        assertTrue(response.tags().contains("sport"));
        assertEquals(true, response.sortOrder());
        assertEquals(createdAt, response.createdAt());
        assertEquals("ok", response.message());
    }
}
