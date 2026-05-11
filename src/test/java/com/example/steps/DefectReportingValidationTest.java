package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End validation test for VW-454.
 * Scenario: When a defect is reported via Temporal (ReportDefectCmd),
 * the system must notify Slack. The Slack notification body MUST contain
 * the GitHub issue URL provided in the command.
 *
 * Corresponding Feature File Concept: S-FB-1
 */
@SpringBootTest(classes = DefectReportingValidationTest.TestConfig.class)
public class DefectReportingValidationTest {

    // This is the interface we would use in the real handler.
    // For the purpose of the TDD Red phase, we might not have the Handler yet,
    // but we can simulate the workflow logic or test the component directly.
    // Here we assume we are testing a Service/Handler that processes the Command.
    
    @Autowired(required = false) // Autowired optional so test runs red before implementation exists
    private DefectReportingService defectReportingService;

    @Autowired
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack.clear();
    }

    @Test
    void testSlackNotificationContainsGitHubUrl() {
        // Given: A defect command with a specific GitHub URL
        String defectId = "S-FB-1";
        String githubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        String expectedChannel = "#vforce360-issues";

        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454",
            "Defect reported by user.",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            githubUrl
        );

        // When: The command is processed
        // Note: Since we are in RED phase, this service might be null or a stub that throws.
        // We execute the logic. If the class doesn't exist, this compilation fails (Red).
        // If it exists but logic is missing, assertion fails (Red).
        try {
            defectReportingService.processReport(cmd);
        } catch (Exception e) {
            // In Red phase, we expect failure, but we specifically want to check the state.
            // However, the TDD instruction says "Fail when run against an empty implementation".
            // If the class doesn't exist, we can't even run this method.
            // Assuming the shell exists or we are creating the shell now.
        }

        // Then: Verify the Slack body includes the GitHub URL
        // We expect the mock to have been called.
        assertFalse(mockSlack.getMessages().isEmpty(), "Slack should have been notified");

        MockSlackNotificationPort.SentMessage msg = mockSlack.getMessages().get(0);
        assertEquals(expectedChannel, msg.channel, "Message should be sent to the correct channel");
        
        // CRITICAL ASSERTION for VW-454
        assertTrue(
            msg.body.contains(githubUrl), 
            "Slack body must include the GitHub issue URL: " + githubUrl + "\nActual body: " + msg.body
        );
    }

    @Test
    void testSlackNotificationFailsIfUrlIsMissing() {
        // Given: A command where the URL is missing or empty (Bad data)
        String githubUrl = ""; 
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1", "Title", "Desc", "LOW", "val", "proj", githubUrl
        );

        // When & Then:
        // We expect the system to either throw an error or post a specific failure message.
        // For this regression, we ensure that if the URL is empty, we don't accidentally post a broken link.
        // Let's assume the service throws an IllegalArgumentException if the URL is blank.
        assertThrows(IllegalArgumentException.class, () -> {
            defectReportingService.processReport(cmd);
        });

        // Verify NO message was sent to Slack in case of validation failure
        assertTrue(mockSlack.getMessages().isEmpty(), "Slack should not be notified for invalid commands");
    }

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }

        @Bean
        public DefectReportingService defectReportingService(SlackNotificationPort slackPort) {
            // In the Red phase, this bean might fail to create if DefectReportingService doesn't exist yet.
            // However, to verify the *logic* of the test, we assume a minimal shell or create a dummy.
            // For the purpose of this output, we define the class below to make it a valid 'Red' test (compiles but fails).
            return new DefectReportingService(slackPort);
        }
    }
}

/**
 * Minimal shell implementation to allow compilation (Red-Green-Refactor cycle).
 * This class represents the code that needs to be written to pass the test.
 */
class DefectReportingService {
    private final SlackNotificationPort slack;

    public DefectReportingService(SlackNotificationPort slack) {
        this.slack = slack;
    }

    public void processReport(ReportDefectCmd cmd) {
        // INTENTIONAL FAILURE / EMPTY IMPLEMENTATION FOR RED PHASE
        // Real implementation would format the message and call slack.send()
        // 
        // Option 1: Do nothing (Test fails because slack.getMessages is empty)
        // Option 2: Throw exception
        
        if (cmd.githubIssueUrl() == null || cmd.githubIssueUrl().isBlank()) {
             throw new IllegalArgumentException("GitHub URL is required");
        }

        // Simulate missing logic: do not call slack.send yet, or call it with wrong data
        // slack.send("#vforce360-issues", "Fix this defect: " + cmd.defectId()); // Missing URL logic
    }
}
