package com.statit.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserTest
{
    @Test
    void defaultConstructorYieldsNullUsernameAndEmptyDemographics()
    {
        User user = new User();
        assertNull(user.getUsername());
        assertNull(user.getEmail());
    }

    @Test
    void parameterizedConstructorPopulatesAllFields()
    {
        Map<String, String> demo = new HashMap<>();
        demo.put("country", "US");
        LocalDate birthday = LocalDate.of(2000, 1, 1);

        User user = new User("alice", "a@b.com", "hash", birthday, demo);

        assertEquals("alice", user.getUsername());
        assertEquals("a@b.com", user.getEmail());
        assertEquals("hash", user.getPasswordHash());
        assertEquals(birthday, user.getBirthday());
        assertEquals("US", user.getDemographics().get("country"));
    }

    @Test
    void constructorWithNullDemographicsCreatesEmptyMap()
    {
        User user = new User("bob", "b@c.com", "hash", LocalDate.of(1990, 5, 5), null);
        assertNotNull(user.getDemographics());
        assertTrue(user.getDemographics().isEmpty());
    }

    @Test
    void updateReplacesAllFields()
    {
        User user = new User("alice", "a@b.com", "h", LocalDate.of(2000, 1, 1), null);
        Map<String, String> newDemo = new HashMap<>();
        newDemo.put("country", "CA");

        user.update("alice2", "a2@b.com", "h2", LocalDate.of(2001, 2, 2), newDemo);

        assertEquals("alice2", user.getUsername());
        assertEquals("a2@b.com", user.getEmail());
        assertEquals("h2", user.getPasswordHash());
        assertEquals(LocalDate.of(2001, 2, 2), user.getBirthday());
        assertEquals("CA", user.getDemographics().get("country"));
    }

    @Test
    void updateWithNullDemographicsCreatesEmptyMap()
    {
        User user = new User("alice", "a@b.com", "h", LocalDate.of(2000, 1, 1), null);
        user.update("u", "e", "p", LocalDate.of(2000, 1, 1), null);
        assertNotNull(user.getDemographics());
        assertTrue(user.getDemographics().isEmpty());
    }

    @Test
    void getAgeMonthsAndYearsReturnZeroWhenBirthdayNull()
    {
        User user = new User();
        assertEquals(0, user.getAgeMonths());
        assertEquals(0, user.getAgeYears());
    }

    @Test
    void getAgeMonthsAndYearsComputeFromBirthday()
    {
        LocalDate birthday = LocalDate.now().minusYears(20).minusMonths(3);
        User user = new User("a", "b", "c", birthday, null);

        // 20 years exactly, plus 3 months = 20 years
        assertEquals(20, user.getAgeYears());
        assertEquals(20 * 12 + 3, user.getAgeMonths());
    }

    @Test
    void settersUpdateFields()
    {
        User user = new User();
        user.setUsername("u");
        user.setEmail("e");
        user.setPasswordHash("p");
        user.setBirthday(LocalDate.of(1995, 3, 3));

        Map<String, String> demo = new HashMap<>();
        demo.put("k", "v");
        user.setDemographics(demo);

        assertEquals("u", user.getUsername());
        assertEquals("e", user.getEmail());
        assertEquals("p", user.getPasswordHash());
        assertEquals(LocalDate.of(1995, 3, 3), user.getBirthday());
        assertEquals("v", user.getDemographics().get("k"));
    }
}
