package com.example.steps;

import com.example.adapters.SlackNotificationPort;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.mocks.InMemoryVForce360Repository;
import com.example.ports.GitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Steps for validating VW-454:
 * Ensures that when a defect is reported, a GitHub issue is created
 * and the resulting URL is successfully relayed to the Slack notification body.
 */
public class VW454Steps {

    // System Under Test
    private VForce360Aggregate aggregate;
    private InMemoryVForce360Repository repository;

    // Mocks
    private GitHubPort mockGitHubPort;
    private SlackNotificationPort mockSlackPort;

    // Inputs
    private ReportDefectCmd command;

    // Verification state
    private Exception caughtException;

    public VW454Steps() {
        // Initialize mock adapters manually or via Spring context if configured
        // Here we instantiate standard mocks for the red-phase
        this.mockGitHubPort = mock(GitHubPort.class);
        this.mockSlackPort = mock(SlackNotificationPort.class);
        this.repository = new InMemoryVForce360Repository(mockGitHubPort, mockSlackPort);
    }

    @Given("a defect report is ready for VForce360")
    public void a_defect_report_is_ready_for_v_force360() {
        // Valid payload based on VForce360 domain constraints
        this.command = new ReportDefectCmd(
            "vw-454",
            "Validating VW-454",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
    }

    @Given("the GitHub service is available")
    public void the_github_service_is_available() {
        // Simulate successful GitHub issue creation
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        when(mockGitHubPort.createIssue(anyString(), anyString(), anyString()))
            .thenReturn(expectedUrl);
    }

    @When("the defect is reported via the temporal-worker exec")
    public void the_defect_is_reported_via_the_temporal_worker_exec() {
        try {
            // In the actual red phase, this Aggregate class might not exist yet,
            // or might not implement the command, causing this to fail.
            aggregate = new VForce360Aggregate(command.defectId());
            
            // Simulate the command execution and repository save
            // (which triggers the GitHub/Slack side effects)
            repository.save(aggregate.execute(command));
            
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // 1. Verify no errors during execution
        if (caughtException != null) {
            fail("Command execution failed unexpectedly in Red Phase: " + caughtException.getMessage(), caughtException);
        }

        // 2. Verify GitHub was called
        verify(mockGitHubPort, times(1)).createIssue(
            eq("[vw-454] Validating VW-454"),
            contains("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"),
            eq("bug")
        );

        // 3. Verify Slack was called with the URL in the body
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlackPort, times(1)).sendNotification(slackBodyCaptor.capture());

        String actualSlackBody = slackBodyCaptor.getValue();
        assertTrue(
            actualSlackBody.contains("https://github.com/bank-of-z/vforce360/issues/454"),
            "Expected Slack body to contain GitHub URL, but got: " + actualSlackBody
        );
    }
}
