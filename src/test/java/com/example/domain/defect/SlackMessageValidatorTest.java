package com.example.domain.defect;

import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Verify Slack message body validation logic.
 * This test ensures the defect where the GitHub URL was missing is fixed.
 */
class SlackMessageValidatorTest {

    private final SlackMessageValidator validator = new SlackMessageValidator();

    @Test
    void should_throw_exception_when_body_is_null() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(null, "http://github.com/issue/1")
        );
        assertTrue(ex.getMessage().contains("Slack body cannot be null"));
    }

    @Test
    void should_throw_exception_when_url_is_null() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("Body text", null)
        );
        assertTrue(ex.getMessage().contains("GitHub URL cannot be null"));
    }

    @Test
    void should_throw_exception_when_body_does_not_contain_url() {
        String body = "This is a defect report without the link.";
        String url = "http://github.com/org/repo/issues/454";

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> validator.validate(body, url)
        );
        // The exact error message might be refined, but we check for the core requirement.
        assertTrue(ex.getMessage().contains("Slack body must include GitHub URL"));
    }

    @Test
    void should_pass_when_body_contains_exact_url() {
        String url = "http://github.com/org/repo/issues/454";
        String body = "Defect reported: " + url;

        assertDoesNotThrow(() -> validator.validate(body, url));
    }

    @Test
    void should_pass_when_body_contains_markdown_formatted_url() {
        String url = "http://github.com/org/repo/issues/454";
        String body = "Defect: <" + url + ">";

        assertDoesNotThrow(() -> validator.validate(body, url));
    }
}
