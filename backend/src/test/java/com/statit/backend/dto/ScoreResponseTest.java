package com.statit.backend.dto;

import com.statit.backend.TestUtils;
import com.statit.backend.model.Category;
import com.statit.backend.model.Score;
import com.statit.backend.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScoreResponseTest
{
    private Score newScore(boolean anonymous)
    {
        User user = new User("alice", "a@x", "h", LocalDate.of(2000, 1, 1), null);
        UUID userId = UUID.randomUUID();
        TestUtils.setField(user, "userId", userId);

        Category category = new Category("Run", "d", "s", null, true, user);
        UUID catId = UUID.randomUUID();
        TestUtils.setField(category, "categoryId", catId);

        Map<String, String> tags = new HashMap<>();
        tags.put("k", "v");
        Score score = new Score(category, user, 42f, tags, anonymous);
        TestUtils.setField(score, "scoreId", UUID.randomUUID());
        return score;
    }

    @Test
    void fromScoreNonAnonymousIncludesUser()
    {
        Score score = newScore(false);
        ScoreResponse response = ScoreResponse.fromScore(score, "ok");

        assertEquals(score.getScoreId(), response.scoreId());
        assertEquals(score.getCategory().getCategoryId(), response.categoryId());
        assertEquals("Run", response.categoryName());
        assertEquals(score.getUser().getUserId(), response.userId());
        assertEquals("alice", response.username());
        assertEquals(42f, response.score());
        assertEquals("v", response.tags().get("k"));
        assertEquals(false, response.anonymous());
        assertEquals("ok", response.message());
    }

    @Test
    void fromScoreAnonymousMasksUser()
    {
        Score score = newScore(true);
        ScoreResponse response = ScoreResponse.fromScore(score, null);

        assertNull(response.userId());
        assertEquals("Anonymous", response.username());
        assertTrue(response.anonymous());
    }
}
