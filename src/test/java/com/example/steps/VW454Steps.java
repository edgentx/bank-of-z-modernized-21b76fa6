package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.service.ValidationService;
import com.example.mocks.InMemoryValidationRepository;
import com.example.mocks.MockSlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class VW454Steps {
    
    private final InMemoryValidationRepository repository = new InMemoryValidationRepository();
    private final MockSlackPort slackPort = new MockSlackPort();
    private ValidationService service;
    private Exception capturedException;
    private String issueUrl = "https://github.com/bank-of-z/issues/454";

    @Given("the defect reporting workflow is initialized")
    public void init() {
        service = new ValidationService(repository, slackPort);
    }

    @When("_report_defect is triggered via temporal-worker exec with GitHub URL {string}")
    public void triggerReportDefect(String url) {
        try {
            service.reportDefect(new ReportDefectCmd(
                "vw-454", 
                "VW-454: GitHub URL in Slack body",
                "Severity: LOW",
                url
            ));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("Slack body contains GitHub issue link")
    public void verifySlackBody() {
        // Verify the aggregate state
        ValidationAggregate aggregate = repository.findById("vw-454");
        Assertions.assertNotNull(aggregate, "Aggregate should be saved");
        Assertions.assertTrue(aggregate.isReported(), "Aggregate should be marked as reported");

        // Verify the external side-effect (Slack)
        Assertions.assertTrue(
            slackPort.notifications.stream().anyMatch(n -> n.contains(issueUrl)),
            "Slack notification should contain the GitHub issue URL: " + issueUrl
        );
    }

    @When("_report_defect is triggered without a GitHub URL")
    public void triggerReportDefectWithoutUrl() {
        try {
            service.reportDefect(new ReportDefectCmd(
                "vw-454-no-url",
                "VW-454: Missing URL",
                "Severity: LOW",
                null // Explicitly null URL
            ));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("Slack body should indicate missing URL or handle gracefully")
    public void verifyGracefulHandling() {
        // Depending on business logic, this might mean sending a message without a link
        // or throwing an error. For this defect fix, we specifically ensure that IF a URL
        // is present, it is in the body.
        Assertions.assertNull(capturedException, "Should not throw exception on missing URL");
    }
}
