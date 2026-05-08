package com.example.domain.validation;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase for S-FB-1.
 * 
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Description: Verify that when a defect is reported, the resulting Slack notification
 * includes a valid GitHub issue URL.
 */
class VW454_GitHubUrlValidationTest {

    private MockSlackNotificationPort mockSlack;
    private DefectReporter defectReporter;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
        // Inject the mock port into the system under test (SUT)
        defectReporter = new DefectReporter(mockSlack);
    }

    @Test
    @DisplayName("S-FB-1: Slack body should contain GitHub URL after reporting defect")
    void testSlackBodyContainsGitHubUrl() {
        // Given: A defect payload similar to VW-454
        String defectId = "VW-454";
        String title = "GitHub URL missing in Slack body";
        String expectedUrlPrefix = "https://github.com";

        // When: The defect is reported via the workflow
        defectReporter.reportDefect(defectId, title, Map.of("severity", "LOW"));

        // Then: Verify the message posted to Slack
        MockSlackNotificationPort.PostedMessage msg = mockSlack.findMessageByChannel("#vforce360-issues");
        
        assertNotNull(msg, "Slack message should be posted to #vforce360-issues");
        
        String body = msg.messageBody();
        
        // The core assertion: The body must contain a link.
        // This will fail initially because the implementation is missing or empty.
        assertTrue(
            body.contains(expectedUrlPrefix), 
            "Slack body should contain GitHub URL: " + expectedUrlPrefix + " but was: " + body
        );
    }

    @Test
    @DisplayName("S-FB-1: Regression test - Verify issue link format")
    void testIssueLinkFormatIsCorrect() {
        // Given: A defect report
        String defectId = "VW-455";
        String title = "Link formatting test";

        // When: Reported
        defectReporter.reportDefect(defectId, title, Map.of());

        // Then: The link should be clickable in Slack (usually wrapped in <url>)
        MockSlackNotificationPort.PostedMessage msg = mockSlack.findMessageByChannel("#vforce360-issues");
        
        assertNotNull(msg);
        
        // Check for Slack link formatting <http...>
        assertTrue(
            msg.messageBody().matches(".*<https://github.com/.*>.*"),
            "Slack body should contain a formatted link <url>."
        );
    }

    /**
     * The System Under Test (SUT).
     * In a real application, this would be the Spring Service or Temporal Activity.
     * For this test suite, we define it locally to force the compilation red state.
     */
    static class DefectReporter {
        private final SlackNotificationPort slackPort;

        public DefectReporter(SlackNotificationPort slackPort) {
            this.slackPort = slackPort;
        }

        public void reportDefect(String id, String title, Map<String, Object> details) {
            // STUB IMPLEMENTATION - CAUSES TESTS TO FAIL
            // This simulates the 'Red' phase of TDD.
            String body = "Defect: " + title; 
            // Intentionally missing the GitHub URL logic to verify the test catches it.
            
            slackPort.postMessage("#vforce360-issues", body);
        }
    }
}
