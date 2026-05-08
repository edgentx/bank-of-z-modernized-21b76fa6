package com.example.e2e.defect;

import com.example.application.DefectReportingActivity;
import com.example.workflow.ReportDefectWorkflow;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.common.converter.DefaultDataConverter;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Regression test for VW-454.
 * Verifies that the Slack body contains the GitHub issue link when reporting a defect.
 * 
 * Corresponds to Story S-FB-1.
 */
public class VW454ValidationTest {

    private static TestWorkflowEnvironment testEnv;
    private Worker worker;
    private static MockSlackActivity mockActivity;

    @BeforeAll
    public static void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        mockActivity = new MockSlackActivity();
    }

    @AfterAll
    public static void tearDown() {
        testEnv.close();
    }

    @BeforeEach
    public void registerWorkflow() {
        worker = testEnv.newWorker("DEFECT_TASK_QUEUE");
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflow.class, () -> new ReportDefectWorkflowImpl());
        worker.registerActivitiesImplementations(mockActivity);
        testEnv.start();
    }

    @Test
    @DisplayName("S-FB-1: Verify Slack body contains GitHub URL")
    public void testSlackBodyContainsGitHubUrl() {
        // Given
        String defectId = "VW-454";
        String description = "Validating VW-454 — GitHub URL in Slack body (end-to-end)";
        mockActivity.clear();

        // When
        ReportDefectWorkflow workflow = testEnv.newWorkflowStub(ReportDefectWorkflow.class);
        workflow.reportDefect(defectId, description);

        // Then
        // Verify that the activity was called exactly once
        assertEquals(1, mockActivity.getCalls().size(), "Slack activity should be called once");
        
        // Verify the content of the message body
        String actualBody = mockActivity.getCalls().get(0).messageBody;
        assertNotNull(actualBody, "Message body should not be null");
        
        // CRITICAL ASSERTION: The GitHub URL must be present
        // The defect report stated "Expected Behavior: Slack body includes GitHub issue: <url>"
        String expectedUrlSubstring = "GitHub Issue:";
        assertTrue(actualBody.contains(expectedUrlSubstring), 
            "Slack body must contain 'GitHub Issue:' token. Actual body: " + actualBody);
        assertTrue(actualBody.startsWith("http"), 
            "Body must contain a valid HTTP URL. Actual body: " + actualBody);
    }

    /**
     * Mock implementation of the DefectReportingActivity to capture output
     * without calling the real Slack API.
     */
    public static class MockSlackActivity implements DefectReportingActivity {
        private final List<CallRecord> calls = new ArrayList<>();

        @Override
        public boolean notifySlack(String defectId, String messageBody) {
            calls.add(new CallRecord(defectId, messageBody));
            return true; // Simulate success
        }

        public List<CallRecord> getCalls() { return calls; }
        public void clear() { calls.clear(); }

        public record CallRecord(String defectId, String messageBody) {}
    }
}