package e2e.regression;

import com.example.workflow.ReportDefectWorkflow;
import com.example.ports.SlackPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test for S-FB-1.
 * Verifies that the Slack body contains the GitHub issue link after reporting a defect.
 */
public class SFB1DefectValidationTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private ReportDefectWorkflow workflow;
    private SlackPort mockSlackPort;

    @BeforeEach
    public void setUp() {
        // Initialize Temporal test environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("DEFECT_TASK_QUEUE");
        
        // Initialize Mocks
        mockSlackPort = mock(SlackPort.class);

        // Register Workflow
        worker.registerWorkflowImplementationFactory(
            ReportDefectWorkflowImpl.class,
            () -> new ReportDefectWorkflowImpl(mockSlackPort) // Assuming impl accepts port via ctor or setter
        );

        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_includesGitHubLinkInSlackBody() {
        // Given
        String description = "VW-454 GitHub URL missing";
        String severity = "LOW";
        
        // When
        // Note: Actual implementation wiring is missing, so this will fail compilation or runtime initially
        // We are defining the shape here.
        String result = workflow.reportDefect(description, severity);

        // Then
        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Verify Slack port was called
        verify(mockSlackPort, times(1)).sendMessage(channelCaptor.capture(), bodyCaptor.capture());

        String actualBody = bodyCaptor.getValue();
        
        // This is the core assertion for S-FB-1
        assertTrue(actualBody.contains("https://github.com"), "Slack body must contain GitHub URL");
    }
}