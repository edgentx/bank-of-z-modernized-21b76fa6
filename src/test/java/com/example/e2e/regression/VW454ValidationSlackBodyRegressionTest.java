package com.example.e2e.regression;

import com.example.Application;
import com.example.mocks.MockGitHubRepositoryAdapter;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression Test for VW-454.
 * <p>
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * <p>
 * Steps:
 * 1. Trigger _report_defect via temporal-worker exec (Simulated by service call)
 * 2. Verify Slack body contains GitHub issue link
 * <p>
 * Expected: Slack body includes GitHub issue: <url>
 */
@SpringBootTest(classes = VW454ValidationSlackBodyRegressionTest.TestConfig.class)
public class VW454ValidationSlackBodyRegressionTest {

    // We are assuming a Service class exists or will be created to handle this logic.
    // Since we are in TDD Red phase, we assume the class name 'DefectReportingService'
    // to act as the System Under Test (SUT).

    @Autowired
    private MockSlackNotificationAdapter slackMock;

    @Autowired
    private MockGitHubRepositoryAdapter githubMock;

    // This would be the real service we are testing.
    // We will mock the instantiation in the test setup if not autowired,
    // but for simplicity, we'll instantiate it manually here with the mock ports
    // to force the test to run in isolation.

    @BeforeEach
    void setUp() {
        slackMock.clear();
        githubMock.resetSequence();
    }

    @Test
    void test_reportDefect_shouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454: Slack body missing link";
        String defectBody = "Verification steps...";
        String channel = "#vforce360-issues";

        // We manually construct the SUT here using the mocks, simulating what
        // the Spring container would do if the implementation existed.
        DefectReportingServiceSUT sut = new DefectReportingServiceSUT(githubMock, slackMock);

        // Act
        // Triggering the _report_defect workflow equivalent
        sut.reportDefect(defectTitle, defectBody, channel);

        // Assert
        // 1. Verify Slack received the message
        assertThat(slackMock.hasReceivedMessage(channel)).isTrue();

        String slackMessageBody = slackMock.getLastMessageBody(channel);

        // 2. Verify the body contains the URL format
        // Expected: "GitHub issue: https://github.com/fake-org/vforce360/issues/1"
        assertThat(slackMessageBody).contains("GitHub issue:");
        assertThat(slackMessageBody).contains("https://github.com/fake-org/vforce360/issues/1");
    }

    @Test
    void test_reportDefect_multipleReports_shouldIncrementGitHubIds() {
        // Arrange
        DefectReportingServiceSUT sut = new DefectReportingServiceSUT(githubMock, slackMock);
        String channel = "#vforce360-issues";

        // Act 1
        sut.reportDefect("Defect 1", "Body 1", channel);

        // Act 2
        sut.reportDefect("Defect 2", "Body 2", channel);

        // Assert
        String secondMessage = slackMock.getLastMessageBody(channel);
        // The second message should contain the URL for issue #2
        assertThat(secondMessage).contains("https://github.com/fake-org/vforce360/issues/2");
    }

    // -----------------------------------------------------
    // Test Doubles (Simulating the implementation to come)
    // -----------------------------------------------------

    /**
     * System Under Test (SUT) Stub.
     * This class represents the implementation we EXPECT to exist.
     * Writing this class allows the test to compile and run (and fail if logic is missing).
     */
    public static class DefectReportingServiceSUT {
        private final GitHubRepositoryPort githubRepo;
        private final SlackNotificationPort slackNotifier;

        public DefectReportingServiceSUT(GitHubRepositoryPort githubRepo, SlackNotificationPort slackNotifier) {
            this.githubRepo = githubRepo;
            this.slackNotifier = slackNotifier;
        }

        public void reportDefect(String title, String body, String channel) {
            // This logic is what we are testing.
            // In the RED phase, this might be empty or wrong, causing the test to fail.
            // In the GREEN phase, this would be implemented correctly.
            
            // Intentionally flawed logic for RED phase demonstration (or simply empty/null return):
            // slackNotifier.sendMessage(channel, "Just a defect"); 
            
            // To make this a valid test file that compiles but requires implementation:
            // We call the port methods.
            String url = githubRepo.createIssue(title, body);
            slackNotifier.sendMessage(channel, "Issue created: " + url); // This might be the wrong format, triggering the test to fail
        }
    }

    @Configuration
    @Import({MockSlackNotificationAdapter.class, MockGitHubRepositoryAdapter.class})
    static class TestConfig {
        // Beans are defined in the mock classes via @Component or @Service,
        // but we explicitly list them here for context if needed.
    }
}