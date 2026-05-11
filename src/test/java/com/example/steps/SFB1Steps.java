package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import com.example.domain.defect.service.DefectWorkflowService;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.SlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SFB1Steps {

    @Autowired
    private DefectWorkflowService workflowService;

    @Autowired
    private InMemoryDefectRepository defectRepository;

    @Autowired
    private MockSlackAdapter mockSlackAdapter;

    private DefectAggregate reportedDefect;
    private Exception caughtException;

    @Given("a defect report for {string}")
    public void a_defect_report_for(String title) {
        // Setup data handled via Temporal trigger in real flow, mocked here
        // We assume the workflow will pick this up or it is created anew
    }

    @When("the defect is reported via the temporal worker")
    public void the_defect_is_reported_via_the_temporal_worker() {
        try {
            // This mimics the Temporal Activity/Workflow execution triggering the service
            String defectId = "DEF-" + System.currentTimeMillis();
            workflowService.reportDefect(defectId, "VW-454", "GitHub URL missing in Slack body");
            reportedDefect = defectRepository.findById(defectId).orElse(null);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body should contain a link to the GitHub issue")
    public void the_slack_body_should_contain_a_link_to_the_github_issue() {
        // 1. Verify no exception during processing
        assertNull(caughtException, "Workflow execution should not throw an exception");

        // 2. Verify Slack adapter was called
        assertFalse(mockSlackAdapter.messages.isEmpty(), "Slack adapter should have received a message");

        // 3. Verify the content of the message
        MockSlackAdapter.Message msg = mockSlackAdapter.messages.get(0);
        assertNotNull(msg.text(), "Slack message body should not be null");
        
        // This is the core validation for S-FB-1
        // The body must contain a URL pointing to the GitHub issue
        assertTrue(msg.text().contains("https://github.com/egdcrypto/bank-of-z/issues/"), 
            "Slack body must contain GitHub issue URL. Found: " + msg.text());
    }
}