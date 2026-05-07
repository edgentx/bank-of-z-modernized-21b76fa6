package com.example.steps;

import com.example.domain.notification.model.DefectReportedEvent;
import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SFB1Steps {

    private NotificationAggregate aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a defect report command for issue {string} with URL {string}")
    public void a_defect_report_command(String issueId, String url) {
        String id = "report-" + issueId;
        this.aggregate = new NotificationAggregate(id);
        // Default valid command, mutated in specific steps if needed
    }

    @When("the defect is reported with title {string} and GitHub URL {string}")
    public void the_defect_is_reported(String title, String githubUrl) {
        try {
            ReportDefectCmd cmd = new ReportDefectCmd(
                aggregate.id(), 
                title, 
                "Severity: LOW", 
                githubUrl
            );
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain {string}")
    public void the_slack_body_should_contain(String expectedLinkText) {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        
        DefectReportedEvent event = (DefectReportedEvent) resultingEvents.get(0);
        assertTrue(event.formattedBody().contains(expectedLinkText), 
            "Body should contain '" + expectedLinkText + "'. Actual: " + event.formattedBody());
    }

    @Then("the validation should fail with error containing {string}")
    public void the_validation_should_fail(String errorMessage) {
        assertNotNull(capturedException, "Expected exception but none was thrown");
        assertTrue(capturedException.getMessage().contains(errorMessage));
    }
}
