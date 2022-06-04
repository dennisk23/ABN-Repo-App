package com.example.abnrepos;

import com.example.abnrepos.data.Repository;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Repository local unit test, which will execute on the development machine (host).
 */
public class RepositoryUnitTest {

    Repository repo = new Repository(12, "name", "full name", "description", "visibility", "html url", false, null);

    @Test
    public void properties_areCorrect() {
        assertEquals(12, repo.getId());
        assertEquals("name", repo.getName());
        assertEquals("full name", repo.getFullName());
        assertEquals("description", repo.getDescription());
        assertEquals("visibility", repo.getVisibility());
        assertEquals("html url", repo.getHtmlUrl());
        assertFalse(repo.isPrivate());
        assertNull(repo.getOwner());
    }
}