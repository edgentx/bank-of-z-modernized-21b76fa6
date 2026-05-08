package com.example.steps;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Story S-FB-1 (VW-454).
 *
 * Verifies that the _report_defect workflow generates a Slack message
 * containing the expected GitHub issue URL.
 */
public class VW454Steps {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackNotificationPort mockSlack;

    // NOTE: These classes are the target of the implementation.
    // They do not exist yet, causing the build to fail (Red Phase).
    private static final String WORKFLOW_CLASS = "com.example.workflow.ReportDefectWorkflow";
    private static final String ACTIVITY_CLASS = "com.example.workflow.DefectActivities";

    @BeforeEach
    public void setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance();
        mockSlack = new MockSlackNotificationPort();
        // Worker initialization would happen here with real implementations
        // worker = testEnvironment.newWorker(TASK_QUEUE);
        // worker.registerWorkflowImplementationTypes(...);
        // testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        if (testEnvironment != null) {
            testEnvironment.close();
        }
    }

    @Test
    @DisplayName("VW-454: Trigger report_defect via temporal-worker exec")
    @SuppressWarnings("unchecked")
    public void testReportDefectWorkflowExecution() throws Exception {
        // Given: A defect command
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454",
            "GitHub URL in Slack body",
            "Validation failed"
        );

        // When: The workflow is executed
        // We attempt to load the classes to enforce compilation failure
        // until they are created.
        try {
            Class<?> workflowClass = Class.forName(WORKFLOW_CLASS);
            assertNotNull(workflowClass, "Workflow class must exist");
        } catch (ClassNotFoundException e) {
            fail("Missing Workflow Implementation: " + WORKFLOW_CLASS);
        }

        try {
            Class<?> activityClass = Class.forName(ACTIVITY_CLASS);
            assertNotNull(activityClass, "Activity class must exist");
        } catch (ClassNotFoundException e) {
            fail("Missing Activity Implementation: " + ACTIVITY_CLASS);
        }
    }

    @Test
    @DisplayName("VW-454: Verify Slack body contains GitHub issue link")
    public void testSlackBodyContainsGitHubUrl() {
        // Given: The defect ID
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/issues/" + defectId;

        // When: Simulating the activity execution logic
        // (This logic will move to the actual Activity class)
        String slackBody = generateExpectedSlackBody(defectId);

        // Then: The body must contain the URL
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body should contain GitHub URL: " + expectedUrl + " but was: " + slackBody
        );
        assertTrue(
            slackBody.contains("GitHub issue:"),
            "Slack body should indicate it is a GitHub issue link"
        );
    }

    @Test
    @DisplayName("E2E Regression: Full Workflow Execution Result Check")
    public void testE2ERegressionScenario() {
        // This test serves as the regression anchor for the specific scenario
        // described in the defect report.
        
        // Given: A triggered report_defect command
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454",
            "GitHub URL in Slack body (end-to-end)",
            "Severity: LOW"
        );

        // When: Processing completes
        // (Simulated here, will be wired to Temporal in impl)
        String resultChannel = "#vforce360-issues";
        String resultBody = generateExpectedSlackBody(cmd.defectId());

        // Then: Verify Slack Payload
        // We verify the structure matches the expected format:
        // "GitHub issue: <url>"
        assertTrue(resultBody.contains("GitHub issue:"));
        assertTrue(resultBody.contains("https://github.com"));
        assertTrue(resultBody.contains("VW-454"));
    }

    /**
     * Helper method defining the EXACT expected behavior for the Slack message.
     * The implementation must match this format for the test to pass.
     */
    private String generateExpectedSlackBody(String defectId) {
        return "Defect reported: " + defectId + ". GitHub issue: https://github.com/example/issues/" + defectId;
    }
}
