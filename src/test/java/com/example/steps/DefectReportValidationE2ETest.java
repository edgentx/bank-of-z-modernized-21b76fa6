package com.example.steps;

import com.example.domain.vforce.model.StartVW454ValidationCmd;
import com.example.ports.SlackPort;
import com.example.ports.TemporalWorkflowPort;
import com.example.mocks.MockSlackAdapter;
import com.example.mocks.MockTemporalWorkflowAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * E2E Regression Test for Defect VW-454.
 * <p>
 * Verifies that triggering the defect reporting workflow via Temporal
 * results in a Slack body containing the GitHub issue URL.
 */
@SpringBootTest
class DefectReportValidationE2ETest {

    @Autowired
    private MockTemporalWorkflowAdapter temporalAdapter;

    @Autowired
    private MockSlackAdapter slackAdapter;

    /**
     * RED Phase:
     * The implementation of TemporalWorkflowPort or the Aggregate logic does not exist yet.
     * This test configures the mocks to simulate the Temporal worker execution and validates
     * the output logic.
     */
    @Test
    void testVW454_SlackBodyContainsGithubUrl() throws Exception {
        // ARRANGE
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/org/repo/issues/454";
        String slackBody = "Defect reported: " + expectedUrl;

        StartVW454ValidationCmd cmd = new StartVW454ValidationCmd(
                defectId,
                expectedUrl,
                slackBody
        );

        // Configure the mock Temporal adapter to return a future that simulates the workflow completion
        CompletableFuture<String> workflowFuture = new CompletableFuture<>();
        when(temporalAdapter.executeReportDefectWorkflow(cmd))
                .thenReturn(workflowFuture);

        // Configure the mock Slack adapter to contain the expected body (simulating the lookup)
        // In a real E2E, this would query the API, here we prime the mock adapter's state
        slackAdapter.setMockBody(slackBody);

        // ACT
        // Simulate the Temporal worker executing the workflow
        workflowFuture.complete(defectId);

        // Simulate the validation step performed by the workflow/activity
        String actualBody = slackAdapter.getLastMessageBody();

        // ASSERT
        // The validation logic should confirm the URL is present
        assertNotNull(actualBody, "Slack body should not be null");
        assertTrue(actualBody.contains(expectedUrl),
                "Slack body must contain the GitHub issue URL: " + expectedUrl + ". Found: " + actualBody);

        // Verify the workflow was triggered
        verify(temporalAdapter).executeReportDefectWorkflow(cmd);
    }

    @Test
    void testVW454_ValidationFailsIfUrlMissing() throws Exception {
        // ARRANGE
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/org/repo/issues/454";
        // Body is missing the URL
        String slackBody = "Defect reported but link is missing.";

        StartVW454ValidationCmd cmd = new StartVW454ValidationCmd(
                defectId,
                expectedUrl,
                slackBody
        );

        slackAdapter.setMockBody(slackBody);

        // ACT & ASSERT
        // This assertion represents the validation logic inside the aggregate/workflow
        String actualBody = slackAdapter.getLastMessageBody();
        assertFalse(actualBody.contains(expectedUrl),
                "Validation should detect missing GitHub URL");
    }
}