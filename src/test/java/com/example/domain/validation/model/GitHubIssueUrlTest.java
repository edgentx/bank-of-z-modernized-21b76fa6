package com.example.domain.validation.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GitHubIssueUrl value object.
 * Covers the validation logic required for defect S-FB-1.
 */
class GitHubIssueUrlTest {

    @Test
    void shouldAcceptValidHttpsGitHubIssueUrl() {
        String validUrl = "https://github.com/tech-co/bank-of-z/issues/454";
        GitHubIssueUrl issueUrl = new GitHubIssueUrl(validUrl);
        
        assertEquals(validUrl, issueUrl.value());
    }

    @Test
    void shouldAcceptValidHttpGitHubIssueUrl() {
        String validUrl = "http://github.com/tech-co/bank-of-z/issues/1";
        GitHubIssueUrl issueUrl = new GitHubIssueUrl(validUrl);
        
        assertEquals(validUrl, issueUrl.value());
    }

    @Test
    void shouldAcceptUrlWithTrailingSlash() {
        String validUrl = "https://github.com/tech-co/bank-of-z/issues/454/";
        GitHubIssueUrl issueUrl = new GitHubIssueUrl(validUrl);
        
        assertEquals(validUrl, issueUrl.value());
    }

    @Test
    void shouldRejectNullUrl() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GitHubIssueUrl(null);
        });

        assertTrue(exception.getMessage().contains("URL cannot be null or empty"));
    }

    @Test
    void shouldRejectEmptyUrl() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GitHubIssueUrl(" ");
        });

        assertTrue(exception.getMessage().contains("URL cannot be null or empty"));
    }

    @Test
    void shouldRejectUrlWithoutIssuesPath() {
        // URL points to repo, not specific issue
        String invalidUrl = "https://github.com/tech-co/bank-of-z";
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GitHubIssueUrl(invalidUrl);
        });

        assertTrue(exception.getMessage().contains("Invalid GitHub Issue URL format"));
    }

    @Test
    void shouldRejectUrlWithNonNumericIssueId() {
        String invalidUrl = "https://github.com/tech-co/bank-of-z/issues/abc";
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GitHubIssueUrl(invalidUrl);
        });

        assertTrue(exception.getMessage().contains("Invalid GitHub Issue URL format"));
    }

    @Test
    void shouldRejectNonGitHubUrl() {
        String invalidUrl = "https://gitlab.com/tech-co/bank-of-z/issues/454";
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GitHubIssueUrl(invalidUrl);
        });

        assertTrue(exception.getMessage().contains("Invalid GitHub Issue URL format"));
    }

    @Test
    void shouldRejectMalformedUrl() {
        String invalidUrl = "github.com/issues/454";
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GitHubIssueUrl(invalidUrl);
        });

        assertTrue(exception.getMessage().contains("Invalid GitHub Issue URL format"));
    }

    @Test
    void equalityShouldBeBasedOnValue() {
        String url = "https://github.com/tech-co/bank-of-z/issues/454";
        GitHubIssueUrl first = new GitHubIssueUrl(url);
        GitHubIssueUrl second = new GitHubIssueUrl(url);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
