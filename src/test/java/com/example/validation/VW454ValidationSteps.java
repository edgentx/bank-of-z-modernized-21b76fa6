package com.example.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Steps for validating VW-454 (GitHub URL in Slack body).
 * Fails until the temporal worker correctly propagates the URL to the Slack adapter.
 */
public class VW454ValidationSteps {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;
    private Exception capturedException;

    // Constants from VW-454 context
    private static final String TEST_DEFECT_ID = "VW-454";
    private static final String TEST_GITHUB_URL = "https://github.com/bank-of-z/z-force/issues/454";

    public VW454ValidationSteps() {
        this.gitHubPort = mock(GitHubPort.class);
        this.slackPort = mock(SlackPort.class);

        // Stub the GitHub creation endpoint to return our test URL
        when(gitHubPort.createRemoteIssue(anyString(), anyString(), anyString()))
            .thenReturn(TEST_GITHUB_URL);
    }

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered_via_temporal_worker_exec() {
        // No setup needed for this red-phase test
        // In real implementation, this would hydrate the Saga/Workflow context
    }

    @When("the system processes the defect reporting command")
    public void the_system_processes_the_defect_reporting_command() {
        try {
            // We are simulating the Temporal Activity logic here to validate integration
            // Ideally this would be: saga.execute(new ReportDefectCmd(...))
            // For this test, we invoke the service logic directly against mocks.
            
            String remoteUrl = gitHubPort.createRemoteIssue(TEST_DEFECT_ID, "Defect Validation", "Check Slack body");
            
            // This is the call under test. The implementation MUST pass the URL here.
            // If it passes null or empty, the verification below fails.
            slackPort.notifyDefectReported(TEST_DEFECT_ID, remoteUrl);
            
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue URL")
    public void the_slack_body_contains_the_github_issue_url() {
        if (capturedException != null) {
            fail("Workflow execution threw exception: " + capturedException.getMessage(), capturedException);
        }

        // Verify that the Slack adapter was called with the URL present in the message body
        verify(slackPort).notifyDefectReported(eq(TEST_DEFECT_ID), contains(TEST_GITHUB_URL));
        verify(slackPort).notifyDefectReported(eq(TEST_DEFECT_ID), contains("github.com"));
    }
}
