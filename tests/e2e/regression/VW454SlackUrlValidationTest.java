package tests.e2e.regression;

import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Regression test for VW-454.
 * Validating that the GitHub URL is present in the Slack body when a defect is reported.
 * 
 * This test file corresponds to the User Story S-FB-1.
 */
public class VW454SlackUrlValidationTest {

    /**
     * TDD Red Phase test.
     * Expected Behavior: Slack body includes GitHub issue: <url>
     * Actual Behavior: Validation likely fails or link is missing.
     */
    @Test
    public void testSlackBodyContainsGitHubIssueUrl() {
        // Given a defect report has been triggered
        String slackBody = "Defect reported. See GitHub issue: http://github.com/example/repo/issues/454";
        
        // When we validate the message body
        SlackMessageValidator validator = new com.example.adapters.SlackMessageValidatorImpl();
        
        // Then it should contain the GitHub URL
        boolean isValid = validator.isValid(slackBody);
        
        if (!isValid) {
            fail("Expected Slack body to contain a GitHub issue URL, but validation failed.\n" +
                 "Body: " + slackBody);
        }
        
        assertTrue(isValid, "Validation failed for body containing GitHub URL");
    }

    @Test
    public void testSlackBodyWithoutGitHubUrlIsInvalid() {
        // Given a defect report without a link
        String slackBody = "Defect reported.";

        // When we validate the message body
        SlackMessageValidator validator = new com.example.adapters.SlackMessageValidatorImpl();

        // Then it should fail validation
        boolean isValid = validator.isValid(slackBody);

        if (isValid) {
            fail("Expected Slack body to be invalid when missing GitHub URL, but validation passed.\n" +
                 "Body: " + slackBody);
        }

        assertTrue(!isValid, "Validation should fail for body missing GitHub URL");
    }
}