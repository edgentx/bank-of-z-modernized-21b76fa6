package com.example.adapters;

import com.example.ports.SlackMessageValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase test for VW-454.
 * Verifies that the SlackMessageValidator properly formats the GitHub issue URL
 * within the Slack message body.
 */
class SlackMessageValidatorImplTest {

    private final SlackMessageValidator validator = new SlackMessageValidatorImpl();

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Given: Inputs from the Temporal workflow
        String defectId = "VW-454";
        String issueTitle = "Validating VW-454";
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        // When: The validator formats the message
        String result = validator.formatSlackMessage(defectId, issueTitle, githubUrl);

        // Then: The result contains the formatted URL line
        // Expected: "Slack body includes GitHub issue: <url>"
        assertTrue(result.contains("GitHub issue:"), "Body should contain 'GitHub issue:' label");
        assertTrue(result.contains(githubUrl), "Body should contain the actual GitHub URL");
    }

    @Test
    void testSlackBodyEscapesUrlFormatting() {
        // Given: Inputs
        String defectId = "VW-454";
        String issueTitle = "Validating VW-454";
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        // When: Formatting message
        String result = validator.formatSlackMessage(defectId, issueTitle, githubUrl);

        // Then: Ensure the formatting characters used in the bug report description are present
        // and valid Java strings.
        assertTrue(result.contains("<" + githubUrl + ">|"), "Body should contain Slack link formatting <|>");
    }
}
