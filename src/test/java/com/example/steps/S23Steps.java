package com.example.steps;

import com.example.domain.validation.ValidationService;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Cucumber Steps for S-23: Validating VW-454.
 * Verifies that when a defect is reported, the resulting Slack body
 * contains the specific GitHub issue URL.
 */
@SpringBootTest
public class S23Steps {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MockSlackNotificationPort mockSlack; // Our injected mock adapter

    private String currentDefectId;
    private String currentGithubUrl;

    @Given("a defect report with ID {string} and GitHub URL {string}")
    public void a_defect_report_with_id_and_github_url(String defectId, String githubUrl) {
        this.currentDefectId = defectId;
        this.currentGithubUrl = githubUrl;
    }

    @When("the defect report is triggered via temporal-worker exec")
    public void the_defect_report_is_triggered() {
        // Simulate the Temporal worker executing the workflow logic
        validationService.reportDefect(currentDefectId, currentGithubUrl);
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        // 1. Verify the Mock was actually called
        assertTrue("Slack notification was not triggered", mockSlack.wasCalled());

        // 2. Retrieve the actual message body sent to the mock
        String actualBody = mockSlack.getLastMessageBody();
        assertNotNull("Message body is null", actualBody);

        // 3. Verify the specific URL is present (Regression for VW-454)
        assertTrue("Slack body should contain GitHub URL: " + currentGithubUrl,
                   actualBody.contains(currentGithubUrl));
    }
}
