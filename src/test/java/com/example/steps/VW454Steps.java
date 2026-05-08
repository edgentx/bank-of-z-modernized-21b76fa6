package com.example.steps;

import com.example.domain.defect.DefectReportedEvent;
import com.example.domain.defect.ReportDefectCommand;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 */
public class VW454Steps {

    // We will wire these manually or via Spring Context in a real integration, 
    // but here we instantiate them directly to drive the TDD Red phase for the domain logic.
    private MockSlackPort slackPort = new MockSlackPort();
    private MockGitHubPort gitHubPort = new MockGitHubPort();
    
    // This is the class under test (Service/Handler) which does not exist yet.
    // We will assume a handler class name 'DefectReportHandler' for the sake of the test.
    private Object defectReportHandler; 
    
    private ReportDefectCommand command;
    private Exception capturedException;

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        // In a real Spring Boot app, this might be @Autowired.
        // Here we just ensure our mocks are ready.
        slackPort = new MockSlackPort();
        gitHubPort = new MockGitHubPort();
        gitHubPort.setMockUrl("http://github.com/test/repo/issues/454");
        
        // Attempting to load the class that SHOULD exist but doesn't yet (Red Phase).
        try {
            // We assume the handler class name based on conventions.
            // If this class is missing, the test fails with ClassNotFound, which is valid Red behavior.
            // However, for unit tests, we usually skip class loading and assume the structure.
            // We will simulate the logic flow inside the @When steps if class loading is too strict,
            // but the prompt asks for FAILING tests.
        } catch (Exception e) {
            // Ignore initialization errors, we expect compile errors mostly.
        }
    }

    @Given("a defect report command exists for VW-454")
    public void a_defect_report_command_exists_for_vw_454() {
        this.command = new ReportDefectCommand(
            "agg-123",
            "VW-454: GitHub URL missing in Slack",
            "Severity: LOW\nComponent: validation",
            Map.of("ticketId", "VW-454")
        );
    }

    @When("the defect report command is executed")
    public void the_defect_report_command_is_executed() {
        // This step simulates the Temporal workflow triggering the service.
        // Since the implementation doesn't exist, we are simulating what the implementation SHOULD do
        // to verify the Contract (Port interfaces).
        
        // Logic we expect to eventually see in the handler:
        // 1. Create GitHub Issue
        // 2. Emit Event
        // 3. Send Slack Notification containing the URL
        
        // To make the test FAIL meaningfully (Red phase), we rely on the fact that
        // the Service class 'DefectReportingService' is missing or the implementation is incomplete.
        
        try {
            // Simulating the call to the non-existent service to trigger compilation failure or dependency injection failure
            Class<?> clazz = Class.forName("com.example.application.DefectReportingService");
            Object instance = clazz.getConstructor(GitHubPort.class, SlackPort.class).newInstance(gitHubPort, slackPort);
            clazz.getMethod("handle", ReportDefectCommand.class).invoke(instance, command);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class 'com.example.application.DefectReportingService' not found. Implementation missing.", e);
        } catch (Exception e) {
            throw new RuntimeException("Reflection failed: " + e.getMessage(), e);
        }
    }

    @Then("the Slack body should include the GitHub issue URL")
    public void the_slack_body_should_include_the_github_issue_url() {
        // This assertion will fail if the mock Slack port didn't receive the specific URL.
        String expectedUrl = gitHubPort.createIssue(null, null); // Retrieving the mock URL configured earlier
        
        // Validate that the message body contains the URL
        assertTrue(
            slackPort.wasUrlSentTo(expectedUrl, "#vforce360-issues"),
            "Slack notification should contain the GitHub issue URL: " + expectedUrl + "\nActual messages: " + slackPort.sentMessages
        );
    }
}
