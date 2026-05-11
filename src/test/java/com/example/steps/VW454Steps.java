package com.example.steps;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockGitHubAdapter;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.ports.VForce360RepositoryPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 */
public class VW454Steps {

    private VForce360RepositoryPort defectRepository;
    private GitHubPort gitHubAdapter;
    private SlackPort slackAdapter;

    private Exception caughtException;

    // We assume a workflow service or application service exists to coordinate this.
    // For the purpose of the red-phase test, we simulate the application logic here.

    @Given("the defect reporting system is initialized")
    public void the_system_is_initialized() {
        defectRepository = new InMemoryDefectRepository();
        gitHubAdapter = new MockGitHubAdapter();
        slackAdapter = new MockSlackAdapter();
        
        // Configure a predictable GitHub URL
        ((MockGitHubAdapter) gitHubAdapter).mockResponse("VW-454 Defect", "https://github.com/egdcrypto/repo/issues/454");
    }

    @When("I trigger report_defect with title {string}")
    public void i_trigger_report_defect(String title) {
        try {
            // 1. Create Aggregate
            DefectAggregate defect = new DefectAggregate("defect-1");
            
            // 2. Execute Command
            defect.execute(new ReportDefectCmd("defect-1", title, "LOW"));
            
            // 3. Save
            defectRepository.save(defect);

            // 4. Call GitHub (via Adapter)
            String url = gitHubAdapter.createIssue(title, "Defect reported from VForce360");

            // 5. Call Slack (via Adapter) with URL
            // This is the line we are testing
            String slackMessage = "Defect reported: " + title + " - " + url;
            slackAdapter.sendMessage(slackMessage);

        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_link() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        
        MockSlackAdapter mockSlack = (MockSlackAdapter) slackAdapter;
        boolean containsLink = mockSlack.lastMessageContains("https://github.com/egdcrypto/repo/issues/454");
        
        // This assertion is the core of the test.
        // It will FAIL in the TDD red phase until the logic is implemented.
        Assertions.assertTrue(containsLink, "Slack body should contain GitHub URL");
    }
}