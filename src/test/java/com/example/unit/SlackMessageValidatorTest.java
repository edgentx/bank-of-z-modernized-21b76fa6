package com.example.unit;

import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SlackMessageValidatorTest {

    @Test
    public void testDetectsGitHubLink() {
        String body = "See the issue at https://github.com/example/repo/issues/454";
        assertTrue(SlackMessageValidator.containsGitHubLink(body));
    }

    @Test
    public void testFailsIfLinkMissing() {
        String body = "This is a plain message without links.";
        assertFalse(SlackMessageValidator.containsGitHubLink(body));
    }

    @Test
    public void testHandlesNull() {
        assertFalse(SlackMessageValidator.containsGitHubLink(null));
    }
}