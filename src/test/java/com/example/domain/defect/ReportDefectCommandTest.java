package com.example.domain.defect;

import com.example.adapters.slack.SlackPort;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Tests for VW-454.
 * 
 * Testing the interaction between Temporal (Workflow) and the external Notification System (Slack).
 * 
 * Acceptance Criteria:
 * 1. The defect report workflow triggers correctly.
 * 2. The Slack notification body contains the GitHub issue URL.
 */
public class ReportDefectCommandTest {

    // System Under Test
    private DefectAggregate defectAggregate;

    // Mocks for External Dependencies (Adapters)
    private SlackPort mockSlackPort;
    private GitHubPort mockGitHubPort;

    @BeforeEach
    void setUp() {
        mockSlackPort = mock(SlackPort.class);
        mockGitHubPort = mock(GitHubPort.class);

        // Inject mocks into the Aggregate (or Service)
        // In a real Spring setup, this might be handled by @InjectMocks, 
        // but we construct manually for pure domain unit tests.
        defectAggregate = new DefectAggregate(mockSlackPort, mockGitHubPort);
    }

    @Test
    void testReportDefect_Success_ShouldPostToSlackWithGitHubLink() {
        // Arrange
        String defectId = "S-FB-1";
        String title = "Fix: Validating VW-454";
        String expectedGitHubUrl = "https://github.com/bank-of-z/issues/454";
        
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, "Critical validation failure in Slack body.");

        // Mock GitHub creation response
        when(mockGitHubPort.createIssue(anyString(), anyString()))
            .thenReturn(expectedGitHubUrl);

        // Act
        defectAggregate.execute(cmd);

        // Assert
        
        // 1. Verify GitHub API was called
        verify(mockGitHubPort).createIssue(eq(title), contains("validation failure"));

        // 2. Capture the Slack payload
        ArgumentCaptor<String> slackMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlackPort).sendMessage(slackMessageCaptor.capture());

        String actualSlackBody = slackMessageCaptor.getValue();

        // 3. ASSERTION FOR VW-454: The Slack body MUST contain the GitHub URL
        // This will FAIL initially if the implementation only posts the title.
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Regression Alert (VW-454): Slack body must include the GitHub Issue URL. Expected: " + expectedGitHubUrl + " but got: " + actualSlackBody
        );
    }

    @Test
    void testReportDefect_GitHubFailure_ShouldThrowException() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd("S-FB-1", "Fail Test", "Attempting to report defect");
        
        // Mock GitHub failure
        when(mockGitHubPort.createIssue(anyString(), anyString()))
            .thenThrow(new RuntimeException("GitHub API Down"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> defectAggregate.execute(cmd));
        
        // Verify Slack was NOT called (poison message prevention)
        verify(mockSlackPort, never()).sendMessage(anyString());
    }
}
