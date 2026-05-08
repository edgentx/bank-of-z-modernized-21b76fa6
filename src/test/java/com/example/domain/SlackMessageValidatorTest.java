package com.example.domain;

import com.example.adapters.SlackMessageValidatorImpl;
import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SlackMessageValidator}.
 * Covers logic for identifying GitHub URLs in text bodies.
 */
class SlackMessageValidatorTest {

    private final SlackMessageValidator validator = new SlackMessageValidatorImpl();

    @Test
    void shouldDetectValidGitHubUrl() {
        String body = "Please see the issue here: <https://github.com/example/bank-of-z/issues/454|VW-454>";
        assertTrue(validator.containsGitHubIssueUrl(body));
    }

    @Test
    void shouldReturnFalseForEmptyBody() {
        assertFalse(validator.containsGitHubIssueUrl(""));
    }

    @Test
    void shouldReturnFalseForBodyWithNoLink() {
        String body = "This is a plain text message without links.";
        assertFalse(validator.containsGitHubIssueUrl(body));
    }

    @Test
    void shouldDetectUrlInComplexString() {
        String body = "Reported via VForce360. <https://github.com/example/repo/issues/1> is the link.";
        assertTrue(validator.containsGitHubIssueUrl(body));
    }
}
