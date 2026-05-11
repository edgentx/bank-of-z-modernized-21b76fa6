package com.example.domain.vforce360;

import com.example.ports.SlackNotifier;
import com.example.ports.GitHubClient;
import com.example.mocks.MockSlackNotifier;
import com.example.mocks.MockGitHubClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 *
 * This is the RED phase. We expect the test to fail because the implementation
 * likely does not exist or returns an empty body, failing the URL check.
 */
class Vw454ValidationTest {

    @Test
    void testSlackBodyContainsGitHubUrl_whenReportDefectIsTriggered() {
        // 1. Setup Mocks (Adapters)
        MockGitHubClient githubClient = new MockGitHubClient();
        MockSlackNotifier slackNotifier = new MockSlackNotifier();

        // 2. Define expected inputs/outputs based on the story
        String defectTitle = "VW-454";
        String defectBody = "Validating GitHub URL in Slack body";
        String expectedUrl = "https://github.com/example-bank/issues/454";
        
        // Configure GitHub mock to return a specific URL when the issue is "created"
        githubClient.setMockResponseUrl(expectedUrl);

        // 3. Instantiate the system under test (SUT)
        // Note: DefectReportingService does not exist yet, but we assume the shape.
        // In the Green phase, this class will be implemented.
        DefectReportingService service = new DefectReportingService(githubClient, slackNotifier);

        // 4. Execute the behavior described in the defect: "Trigger _report_defect via temporal-worker exec"
        service.reportDefect(defectTitle, defectBody);

        // 5. Verify Expected Behavior: "Slack body includes GitHub issue: <url>"
        String actualSlackMessage = slackNotifier.getLastMessageBody();
        
        // This assertion ensures the link is present. 
        // We expect this to fail if the system only sends "Defect Reported" without the link.
        assertTrue(
            actualSlackMessage.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL: " + expectedUrl + "\nActual body: " + actualSlackMessage
        );
    }
}
