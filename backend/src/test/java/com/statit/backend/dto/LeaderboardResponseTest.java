package com.statit.backend.dto;

import com.statit.backend.TestUtils;
import com.statit.backend.model.Category;
import com.statit.backend.model.Score;
import com.statit.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardResponseTest
{
    private Score buildScore(String username, float scoreValue, boolean anonymous, Category category)
    {
        User user = new User(username, username + "@x", "h", LocalDate.of(2000, 1, 1), null);
        TestUtils.setField(user, "userId", UUID.randomUUID());
        Score score = new Score(category, user, scoreValue, null, anonymous);
        TestUtils.setField(score, "scoreId", UUID.randomUUID());
        return score;
    }

    @Test
    void fromPageBuildsRankedEntries()
    {
        User founder = new User("f", "f@x", "h", LocalDate.of(2000, 1, 1), null);
        Category category = new Category("Cat", "d", "u", null, true, founder);
        UUID categoryId = UUID.randomUUID();
        TestUtils.setField(category, "categoryId", categoryId);

        Score s1 = buildScore("alice", 10f, false, category);
        Score s2 = buildScore("bob", 8f, true, category);
        List<Score> content = Arrays.asList(s1, s2);
        Page<Score> page = new PageImpl<>(content, PageRequest.of(1, 2), 5);

        LeaderboardResponse response = LeaderboardResponse.fromPage(category, page);

        assertEquals(categoryId, response.categoryId());
        assertEquals("Cat", response.categoryName());
        assertEquals("u", response.units());
        assertEquals(true, response.sortOrder());
        assertEquals(1, response.page());
        assertEquals(5, response.totalElements());
        assertEquals(2, response.scores().size());

        // Page 1 with size 2 -> ranks 3 and 4
        assertEquals(3, response.scores().get(0).rank());
        assertEquals("alice", response.scores().get(0).username());
        assertEquals(4, response.scores().get(1).rank());
        assertEquals("Anonymous", response.scores().get(1).username());
    }

    @Test
    void fromPageHandlesEmptyContent()
    {
        User founder = new User("f", "f@x", "h", LocalDate.of(2000, 1, 1), null);
        Category category = new Category("Cat", "d", "u", null, false, founder);
        Page<Score> empty = new PageImpl<>(List.of(), PageRequest.of(0, 25), 0);

        LeaderboardResponse response = LeaderboardResponse.fromPage(category, empty);
        assertTrue(response.scores().isEmpty());
        assertEquals(0, response.page());
        assertEquals(0, response.totalElements());
    }
}
