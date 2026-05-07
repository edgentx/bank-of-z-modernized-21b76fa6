package com.example.steps;

import com.example.application.DefectReportingService;
import com.example.domain.shared.*;
import com.example.mocks.MockNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454.
 * Ensures the Slack notification body contains the GitHub URL.
 */
public class VW454Steps {

    private final MockNotificationPort mockNotificationPort = new MockNotificationPort();
    private DefectReportingService service;
    private Exception caughtException;

    // Test Data
    private static final String DEFECT_ID = "S-FB-1";
    private static final String TITLE = "Validating VW-454 - GitHub URL in Slack body";
    private static final String DESCRIPTION = "Reproduction Steps...";
    private static final String SEVERITY = "LOW";

    @Given("the defect reporting system is initialized")
    public void the_system_is_initialized() {
        service = new DefectReportingService(mockNotificationPort);
        mockNotificationPort.reset();
        caughtException = null;
    }

    @Given("a defect report command is ready")
    public void a_defect_report_command_is_ready() {
        // Command construction logic is implicit in the When step for simplicity,
        // or we could store state here if needed.
    }

    @When("the defect report is triggered via temporal-worker exec")
    public void the_defect_report_is_triggered() {
        ReportDefectCommand cmd = new ReportDefectCommand(
            DEFECT_ID,
            TITLE,
            DESCRIPTION,
            SEVERITY,
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        try {
            service.handle(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body includes GitHub issue")
    public void the_slack_body_includes_github_issue() {
        // 1. Verify no exceptions occurred
        assertNull(caughtException, "Service execution should not throw an exception");

        // 2. Verify External Dependencies were called in order (GitHub -> Slack)
        assertEquals(1, mockNotificationPort.githubCalls.size(), "GitHub issue should be created");
        assertEquals(1, mockNotificationPort.slackCalls.size(), "Slack should be notified");

        // 3. Verify Content (The Core Validation for VW-454)
        String actualSlackBody = mockNotificationPort.slackCalls.get(0).message();
        String expectedUrl = mockNotificationPort.githubUrlToReturn;

        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body should contain the GitHub URL: " + expectedUrl + "\nActual: " + actualSlackBody
        );
    }
}
