package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454.
 * End-to-end regression test covering the temporal-worker trigger to Slack body generation.
 */
public class SFB1Steps {

    private String defectId;
    private String githubUrl;
    private DefectAggregate aggregate;
    private DefectReportedEvent resultEvent;
    private Exception thrownException;

    @Given("a defect report is triggered for VW-454")
    public void a_defect_report_is_triggered_for_vw_454() {
        this.defectId = "VW-454";
        this.githubUrl = "https://github.com/example/bank-of-z/issues/454";
        this.aggregate = new DefectAggregate(defectId);
    }

    @When("the temporal-worker executes _report_defect logic")
    public void the_temporal_worker_executes_report_defect_logic() {
        try {
            ReportDefectCmd cmd = new ReportDefectCmd(
                defectId,
                "Fix: Validating VW-454",
                "Slack body missing URL",
                githubUrl,
                "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
            );
            var events = aggregate.execute(cmd);
            this.resultEvent = (DefectReportedEvent) events.get(0);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        assertNull(thrownException, "Should not throw exception: " + (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(resultEvent, "Event should not be null");
        
        String body = resultEvent.slackBody();
        
        // Regression check for the defect
        assertTrue(body.contains("GitHub issue:"), "Missing 'GitHub issue:' prefix in body: " + body);
        assertTrue(body.contains(githubUrl), "Missing URL in body: " + body);
    }
}