package com.statit.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTest
{
    private User newUser()
    {
        return new User("u", "u@x.com", "h", LocalDate.of(2000, 1, 1), null);
    }

    private Category newCategory()
    {
        return new Category("c", "d", "u", null, true, newUser());
    }

    @Test
    void defaultConstructorLeavesFieldsNull()
    {
        Score score = new Score();
        assertNull(score.getCategory());
        assertNull(score.getUser());
        assertNull(score.getScore());
    }

    @Test
    void parameterizedConstructorPopulatesAllFields()
    {
        Category category = newCategory();
        User user = newUser();
        Map<String, String> tags = new HashMap<>();
        tags.put("region", "us");

        Score score = new Score(category, user, 12.5f, tags, true);

        assertSame(category, score.getCategory());
        assertSame(user, score.getUser());
        assertEquals(12.5f, score.getScore());
        assertEquals("us", score.getTags().get("region"));
        assertTrue(score.getAnonymous());
    }

    @Test
    void constructorWithNullTagsCreatesEmptyMap()
    {
        Score score = new Score(newCategory(), newUser(), 1f, null, false);
        assertNotNull(score.getTags());
        assertTrue(score.getTags().isEmpty());
    }

    @Test
    void updateReplacesAllFields()
    {
        Score score = new Score(newCategory(), newUser(), 1f, null, false);
        Category newCategory = newCategory();
        User newUser = newUser();
        Map<String, String> newTags = new HashMap<>();
        newTags.put("a", "b");

        score.update(newCategory, newUser, 2f, newTags, true, true);

        assertSame(newCategory, score.getCategory());
        assertSame(newUser, score.getUser());
        assertEquals(2f, score.getScore());
        assertEquals("b", score.getTags().get("a"));
        assertTrue(score.getAnonymous());
    }

    @Test
    void updateWithNullTagsCreatesEmptyMap()
    {
        Score score = new Score(newCategory(), newUser(), 1f, null, false);
        score.update(newCategory(), newUser(), 2f, null, true, false);
        assertNotNull(score.getTags());
        assertTrue(score.getTags().isEmpty());
    }

    @Test
    void settersUpdateFields()
    {
        Score score = new Score(newCategory(), newUser(), 1f, null, false);
        score.setRejected(true);
        Map<String, String> tags = new HashMap<>();
        tags.put("k", "v");
        score.setTags(tags);

        assertEquals("v", score.getTags().get("k"));
    }
}
