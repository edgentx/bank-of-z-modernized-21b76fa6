package com.example.workflows;

import com.example.application.DefectReportingActivity;
import io.temporal.testing.TestWorkflowRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for ReportDefectWorkflow.
 * Verifies the orchestration logic (Workflow) using TestWorkflowRule and mocked Activities.
 */
public class ReportDefectWorkflowTest {

    // Mock Activity implementation
    @Mock
    private DefectReportingActivity mockActivity;

    /**
     * JUnit4 Rule for Temporal Workflow testing.
     * Manages the lifecycle of the workflow execution in memory.
     */
    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .addWorkflowActivityImplementations(
                            new ReportDefectWorkflowImpl(), // Workflow Under Test
                            new MockDefectReportingActivity() // Mock Activity Implementation
                    )
                    .setDoNotStart(true) // Manual control if needed, though constructor usually starts it.
                    .build();

    /**
     * Custom Mock Activity injected into the test rule.
     * We define this inner class to simulate the behavior of the Activity
     * without connecting to real external services (GitHub/Slack).
     */
    public static class MockDefectReportingActivity implements DefectReportingActivity {
        private String simulatedGitHubUrl = "https://github.com/example/repo/issues/123";
        private boolean lastPostSuccess = false;
        private String lastSlackBody;

        @Override
        public boolean postToSlack(String channel, String body) {
            this.lastSlackBody = body;
            this.lastPostSuccess = true;
            // Simulate successful API call
            return true;
        }

        @Override
        public String createGitHubIssue(String title, String body) {
            // Simulate returning a valid URL
            return simulatedGitHubUrl;
        }

        public String getLastSlackBody() {
            return lastSlackBody;
        }
    }

    @Test
    public void testReportDefect_includesGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454: Validation Error";
        String defectBody = "The validation logic is missing a check.";

        // Get the instance of the mock activity to verify interactions later
        MockDefectReportingActivity activityMock = (MockDefectReportingActivity) 
            testWorkflowRule.getTypedActivityOptions(DefectReportingActivity.class);
        
        // Create a fresh mock instance to assert against, or capture the one used by the rule.
        // In TestWorkflowRule, we can directly cast the implementation if we registered it.
        MockDefectReportingActivity spyActivity = new MockDefectReportingActivity();
        
        // Re-initialize rule with specific mock instance we can spy on
        TestWorkflowRule rule = TestWorkflowRule.newBuilder()
                .addWorkflowActivityImplementations(new ReportDefectWorkflowImpl(), spyActivity)
                .build();
        
        // Act
        ReportDefectWorkflow workflow = rule.newWorkflowStub(ReportDefectWorkflow.class);
        String resultUrl = workflow.reportDefect(defectTitle, defectBody);

        // Assert
        // 1. Verify GitHub URL is returned
        assertNotNull("Workflow should return a GitHub URL", resultUrl);
        assertTrue("URL should contain github.com", resultUrl.contains("github.com"));

        // 2. Verify Slack was called
        // 3. Verify Slack body contains the GitHub URL (Acceptance Criteria)
        String slackBody = spyActivity.getLastSlackBody();
        assertNotNull("Slack body should not be null", slackBody);
        assertTrue(
            "Slack body must contain the GitHub issue link. Body was: " + slackBody,
            slackBody.contains(resultUrl)
        );
    }
}