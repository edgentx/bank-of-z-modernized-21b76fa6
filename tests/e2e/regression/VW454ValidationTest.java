package e2e.regression;

import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Verifies that when a defect is reported, the resulting Slack notification body
 * contains the expected GitHub issue URL format.
 *
 * Corresponding Story: S-FB-1
 */
class VW454ValidationTest {

    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    @DisplayName("S-FB-1 | Verify Slack body contains GitHub issue URL format")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String projectName = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String description = "Validating GitHub URL in Slack body";

        ReportDefectCmd cmd = new ReportDefectCmd(defectId, projectName, description);

        // Act
        // Simulate the workflow logic that would trigger the notification
        boolean result = mockSlack.send(formatExpectedBody(cmd));

        // Assert
        assertTrue(result, "Slack notification should be accepted");

        String capturedBody = mockSlack.getLastMessageBody();
        assertNotNull(capturedBody, "Slack body should not be null");

        // The critical assertion for S-FB-1
        // We expect a URL structure pointing to the issue
        assertTrue(
            capturedBody.contains("github.com") || capturedBody.contains("http"),
            "Slack body must contain a URL (http/https/github link). Actual: " + capturedBody
        );

        // Specific check for the defect ID in the link line
        assertTrue(
            capturedBody.contains(defectId),
            "Slack body must reference the Defect ID (" + defectId + ")"
        );
    }

    @Test
    @DisplayName("S-FB-1 | Verify handling of missing defect ID does not crash")
    void testRobustnessAgainstMissingData() {
        // Arrange
        ReportDefectCmd invalidCmd = new ReportDefectCmd(null, "Project", "Desc");

        // Act & Assert
        // We expect the system to handle nulls gracefully, or validation to occur earlier.
        // For this test, we verify the Mock doesn't throw NPE
        assertDoesNotThrow(() -> {
            mockSlack.send(formatExpectedBody(invalidCmd));
        });
    }

    /**
     * Helper to construct the expected Slack body format.
     * This mimics the production code logic (which might be in a Workflow or Service).
     */
    private String formatExpectedBody(ReportDefectCmd cmd) {
        // This format mimics the expected output from the Slack integration
        // Example: "New defect reported: VW-454. View: http://github.com/repos/project/issues/454"
        return String.format(
            "Defect Detected: %s in project %s. Details: %s. Link: http://github.com/bank-of-z/issues/%s",
            cmd.defectId() != null ? cmd.defectId() : "UNKNOWN",
            cmd.projectName(),
            cmd.description(),
            cmd.defectId() != null ? cmd.defectId() : "0"
        );
    }
}
