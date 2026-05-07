package com.example.e2e.regression;

import com.example.Application;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression Test for Defect VW-454.
 * <p>
 * Expected Behavior: When a defect is reported, a GitHub issue is created,
 * and the resulting URL is included in the Slack notification body.
 * <p>
 * Corresponding Story ID: S-FB-1
 */
@SpringBootTest(classes = VW454SlackGitHubLinkTest.TestConfig.class)
public class VW454SlackGitHubLinkTest {

    @Autowired(required = false) // Implementation pending
    private Application.ReportDefectWorkflowOrchestrator workflow;

    @Autowired
    private MockSlackNotificationPort mockSlack;

    @Autowired
    private MockGitHubPort mockGitHub;

    @BeforeEach
    void setUp() {
        mockSlack.clear();
        mockGitHub.setNextIssueUrl("https://github.com/example/bank-of-z/issues/454");
    }

    @Test
    void testReportDefect_postsSlackNotificationContainingGitHubUrl() {
        // ARRANGE
        // Context: Triggering _report_defect via temporal-worker exec
        String defectTitle = "Defect VW-454";
        String defectDescription = "GitHub URL missing in Slack body";
        String targetChannel = "#vforce360-issues";

        // ACT
        // This simulates the Temporal worker executing the report_defect workflow
        // Note: This will fail/throw until Application.java implements the wiring.
        try {
            workflow.execute(targetChannel, defectTitle, defectDescription);
        } catch (Exception e) {
            // Expected in RED phase - ignoring to assert state of mocks
            // or we could use assumeTrue if we wanted to skip, but failing is better for TDD.
        }

        // ASSERT
        // Verify GitHub Issue was requested (or created via Port)
        // (If the workflow didn't run, this will be empty/0)
        
        // 1. Verify Slack Interaction
        var messages = mockSlack.getSentMessages();
        boolean foundSlackMessage = false;
        String lastSlackBody = "";
        
        for (var msg : messages) {
            if (msg.channel.equals(targetChannel)) {
                foundSlackMessage = true;
                lastSlackBody = msg.text;
            }
        }

        assertTrue(foundSlackMessage, "Slack notification should be sent to #vforce360-issues");

        // 2. Verify URL content
        // Acceptance Criteria: "Slack body includes GitHub issue: <url>"
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        
        assertTrue(
            lastSlackBody.contains(expectedUrl),
            "Expected Slack body to contain GitHub URL: " + expectedUrl + " but was: " + lastSlackBody
        );
        
        // Pattern check for robustness (as per Story description)
        assertTrue(
            lastSlackBody.contains("http") && lastSlackBody.contains("github.com"),
            "Slack body should look like a GitHub URL"
        );
    }

    @Configuration
    @Import(Application.class) // Import main app configuration to pick up components if they exist
    static class TestConfig {
        
        @Bean
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }

        @Bean
        public GitHubPort gitHubPort() {
            return new MockGitHubPort();
        }
    }
}
