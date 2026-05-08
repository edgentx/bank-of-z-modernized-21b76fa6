package com.example.steps;

import com.example.domain.validation.ValidationService;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Regression test for VW-454.
 * Validates that the temporal-worker (simulated here) correctly produces a Slack body
 * containing the GitHub issue URL.
 */
public class VW454ValidationSteps {

    // Using Mockito to simulate Temporal Worker dependencies
    private final SlackNotificationPort slackPort = mock(SlackNotificationPort.class);
    private final GitHubIssuePort githubPort = mock(GitHubIssuePort.class);
    
    private final ValidationService service = new ValidationService(slackPort, githubPort);

    private ArgumentCaptor<String> capturedSlackBody;

    @Given("the defect report for VW-454 is triggered")
    public void the_defect_report_is_triggered() {
        // Setup mocks
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        when(githubPort.createIssue(anyString(), anyString())).thenReturn(expectedUrl);
    }

    @When("the temporal worker executes the report_defect workflow")
    public void the_worker_executes() {
        // Simulate the workflow execution
        String defectId = "S-FB-1";
        String title = "Fix: Validating VW-454";
        String description = "Severity: LOW";
        
        service.reportDefect(defectId, title, description);

        // Capture the output sent to Slack
        capturedSlackBody = ArgumentCaptor.forClass(String.class);
        verify(slackPort).sendMessage(capturedSlackBody.capture());
    }

    @Then("the Slack body includes the GitHub issue URL")
    public void the_slack_body_includes_url() {
        String body = capturedSlackBody.getValue();
        assertNotNull(body, "Slack body should not be null");
        
        // Defect VW-454 specifically checks for the presence of the URL in the body
        assertTrue(body.contains("https://github.com"), "Body should contain GitHub URL");
        assertTrue(body.contains("<") && body.contains(">"), "Body should contain Slack formatted link");
    }
}
