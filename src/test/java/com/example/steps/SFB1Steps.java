package com.example.steps;

import com.example.defect.domain.DefectAggregate;
import com.example.defect.domain.ReportDefectCmd;
import com.example.defect.repository.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.Assert.*;

import java.util.Map;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * This test suite validates the end-to-end behavior of defect reporting,
 * ensuring the generated URL contains the defect ID.
 */
public class SFB1Steps {

    private DefectRepository repository = new InMemoryDefectRepository();
    private DefectAggregate aggregate;
    private Exception capturedException;

    @Given("a defect report request for VW-454 is triggered")
    public void a_defect_report_request_for_vw_454_is_triggered() {
        String defectId = "VW-454";
        this.aggregate = new DefectAggregate(defectId);
        // Store it for later retrieval if needed
        repository.save(aggregate);
    }

    @When("the temporal worker executes the report_defect command")
    public void the_temporal_worker_executes_the_report_defect_command() {
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Fix: Validating VW-454",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            Map.of()
        );
        try {
            var events = aggregate.execute(cmd);
            // In a real scenario, the repository would be updated here
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // Verify the aggregate state which represents the data available to the Slack integration
        assertTrue("Aggregate should be marked as reported", aggregate.isReported());
        
        String url = aggregate.getGithubUrl();
        assertNotNull("GitHub URL should not be null", url);
        
        // Validate the URL structure contains the defect ID
        assertTrue("URL should contain defect ID VW-454", url.contains("VW-454"));
        assertTrue("URL should start with https://github.com", url.startsWith("https://github.com"));
    }
}
