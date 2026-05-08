package com.example.steps;

import com.example.domain.shared.ReportDefectCmd;
import com.example.mocks.MockSlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for Story S-FB-1.
 * Validates that the Slack body contains the GitHub issue URL.
 */
public class SFB1Steps {

    // Injected via Cucumber Spring context
    private MockSlackPort mockSlack;
    private String defectId;
    private String githubUrl;
    private Exception capturedException;

    public void setMockSlack(MockSlackPort mockSlack) {
        this.mockSlack = mockSlack;
    }

    @Given("a defect report with ID {string} and GitHub URL {string}")
    public void a_defect_report_with_id_and_github_url(String id, String url) {
        this.defectId = id;
        this.githubUrl = url;
        // Assume mockSlack is autowired or set up in the test suite context
        if (this.mockSlack == null) {
            throw new IllegalStateException("MockSlackPort not initialized");
        }
        this.mockSlack.clear();
    }

    @When("the _report_defect command is executed via temporal-worker")
    public void the_report_defect_command_is_executed_via_temporal_worker() {
        try {
            // Simulate the execution of the command using the domain/workflow logic
            // In a real scenario, this would trigger the Temporal workflow
            // For the RED phase, we simulate the failure condition or missing behavior
            
            var cmd = new ReportDefectCmd(defectId, "VW-454: GitHub URL Validation", githubUrl);
            
            // This logic represents the 'Worker' processing the command
            // We expect this to eventually call SlackPort
            
            // SIMULATED ACTUAL BEHAVIOR (Red Phase Target):
            // For now, we do nothing or call Slack with bad data to force the test to fail
            // if the implementation is missing.
            
            // If the implementation existed, it would look something like:
            // workflow.reportDefect(cmd);
            
            // To ensure the test fails initially (Red phase), we can intentionally NOT call the mock
            // or we can implement a placeholder that checks if the implementation exists.
            // However, the instruction implies we write the test *expecting* correct behavior.
            
            // Let's simulate the 'report_defect' invocation logic which should eventually hit the Slack port.
            // Since we are in the test setup, we manually invoke the logic path that should be implemented.
            
            // MOCK IMPLEMENTATION OF THE WORKER (to be replaced by real impl)
            // If the feature is not implemented, the list will be empty, causing the 'Then' step to fail.
            
            // For demonstration of the RED phase, we will NOT invoke the mock correctly here.
            // The test below expects the URL. If the code isn't there, the mock list is empty.
            
            // Note: In a pure TDD red phase, the code below would be the test logic *only*.
            // The system under test (SUT) would be empty/failing.
            
            // Here we assume the SUT (Application.java or Worker) will handle this.
            // We just need to verify the side-effect on the mock.
            
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack notification body must contain the GitHub URL {string}")
    public void the_slack_notification_body_must_contain_the_github_url(String expectedUrl) {
        // 1. Verify no exceptions during processing
        assertNull(capturedException, "Processing should not throw exception");

        // 2. Verify that a message was actually sent
        var messages = mockSlack.getSentMessages();
        assertFalse(messages.isEmpty(), "Slack notification should have been sent");

        // 3. Verify the content includes the specific GitHub URL (Acceptance Criteria)
        String body = messages.get(0);
        assertTrue(
            body.contains(expectedUrl), 
            "Slack body should contain GitHub issue URL: " + expectedUrl + ". Actual body: " + body
        );
    }
}
