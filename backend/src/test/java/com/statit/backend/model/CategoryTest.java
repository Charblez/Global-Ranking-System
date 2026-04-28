package com.statit.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest
{
    private User newUser()
    {
        return new User("founder", "f@x.com", "h", LocalDate.of(2000, 1, 1), null);
    }

    @Test
    void defaultConstructorLeavesFieldsNull()
    {
        Category category = new Category();
        assertNull(category.getName());
        assertNull(category.getUnits());
    }

    @Test
    void parameterizedConstructorPopulatesAllFields()
    {
        User founder = newUser();
        List<String> tags = Arrays.asList("Sport", "Outdoor");

        Category category = new Category("Running", "100m sprint", "seconds", tags, false, founder);

        assertEquals("Running", category.getName());
        assertEquals("100m sprint", category.getDescription());
        assertEquals("seconds", category.getUnits());
        assertEquals(false, category.getSortOrder());
        assertSame(founder, category.getFoundingUser());
        assertTrue(category.getTags().contains("sport"));
        assertTrue(category.getTags().contains("outdoor"));
    }

    @Test
    void constructorWithNullTagsLeavesEmptyTags()
    {
        Category category = new Category("Pushups", "desc", "reps", null, true, newUser());
        assertNotNull(category.getTags());
        assertTrue(category.getTags().isEmpty());
    }

    @Test
    void addTagLowercasesAndDeduplicates()
    {
        Category category = new Category("c", "d", "u", null, true, newUser());
        category.addTag("Sports");
        category.addTag("sports");
        category.addTag("SPORTS");

        assertEquals(1, category.getTags().size());
        assertEquals("sports", category.getTags().get(0));
    }

    @Test
    void addTagsAddsAllUnique()
    {
        Category category = new Category("c", "d", "u", null, true, newUser());
        category.addTags(Arrays.asList("A", "b", "a"));
        assertEquals(2, category.getTags().size());
    }

    @Test
    void removeTagRemovesIfPresent()
    {
        Category category = new Category("c", "d", "u", Arrays.asList("a", "b"), true, newUser());
        category.removeTag("A");
        assertFalse(category.getTags().contains("a"));
        assertTrue(category.getTags().contains("b"));
    }

    @Test
    void removeTagIsNoOpIfMissing()
    {
        Category category = new Category("c", "d", "u", Arrays.asList("a"), true, newUser());
        category.removeTag("z");
        assertEquals(1, category.getTags().size());
    }

    @Test
    void updateReplacesFieldsAndAddsNewTags()
    {
        Category category = new Category("c", "d", "u", Arrays.asList("a"), true, newUser());
        category.update("c2", "d2", "u2", Arrays.asList("b"), false);

        assertEquals("c2", category.getName());
        assertEquals("d2", category.getDescription());
        assertEquals("u2", category.getUnits());
        assertEquals(false, category.getSortOrder());
        assertTrue(category.getTags().contains("a"));
        assertTrue(category.getTags().contains("b"));
    }

    @Test
    void updateWithNullTagsKeepsExistingTags()
    {
        Category category = new Category("c", "d", "u", Arrays.asList("a"), true, newUser());
        category.update("c2", "d2", "u2", null, false);
        assertTrue(category.getTags().contains("a"));
    }

    @Test
    void settersUpdateFields()
    {
        Category category = new Category();
        category.setName("name");
        category.setDescription("desc");
        category.setTags(Arrays.asList("x"));

        assertEquals("name", category.getName());
        assertEquals("desc", category.getDescription());
        assertEquals(1, category.getTags().size());
    }
}
