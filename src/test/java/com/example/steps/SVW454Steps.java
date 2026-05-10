package com.example.steps;

import com.example.domain.validation.ValidationAggregate;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for VW-454: GitHub URL in Slack body.
 * Corresponds to the temporal-worker defect report scenario.
 */
public class SVW454Steps {

    private ValidationAggregate aggregate;
    private DefectReportedEvent resultEvent;
    private Exception reportedException;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered_via_temporal_worker_exec() {
        // Simulating the worker receiving a signal to report a defect
        String defectId = "VW-454";
        this.aggregate = new ValidationAggregate(defectId);
    }

    @When("the system processes the defect report command")
    public void the_system_processes_the_defect_report_command() {
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                this.resultEvent = (DefectReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            this.reportedException = e;
        }
    }

    @Then("the resulting event payload contains the GitHub issue link")
    public void the_resulting_event_payload_contains_the_github_issue_link() {
        // RED PHASE ASSERTION
        // This test will fail if the URL is missing, null, or malformed (e.g. "PLACEHOLDER")
        assertNotNull(resultEvent, "Event should not be null");
        
        String url = resultEvent.githubUrl();
        assertNotNull(url, "GitHub URL must not be null in the event payload");
        
        // Verifying it is a valid URL format for Slack
        assertTrue(url.startsWith("http"), "URL must start with http/https");
        assertFalse(url.equals("PLACEHOLDER"), "URL must not be a placeholder constant");
    }
}