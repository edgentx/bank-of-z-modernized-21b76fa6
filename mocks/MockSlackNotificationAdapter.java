package mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * <p>
 * This adapter provides the S-FB-1 "Red Phase" behavior.
 * It currently returns an incomplete or static string,
 * causing the VW454SlackLinkValidationTest to fail until the implementation is corrected.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    /**
     * Current implementation returns a placeholder.
     * This will cause {@link com.example.e2e.regression.VW454SlackLinkValidationTest#whenDefectReported_thenSlackBodyContainsGitHubUrl}
     * to fail because it lacks the specific GitHub URL.
     */
    @Override
    public String formatDefectNotification(String defectId) {
        // RED PHASE: Broken implementation.
        // Returning a message that does NOT contain the GitHub URL as required by S-FB-1.
        return "Defect reported: " + defectId + ". Please check internal systems.";
        
        // To fix (Green phase), this logic should be:
        // return String.format("Defect reported: %s. See <%s|GitHub>", defectId, constructUrl(defectId));
    }

    @Override
    public void sendNotification(String messageBody) {
        // No-op for mock testing
    }

    // Helper that exists in the system but isn't being used yet (Red Phase)
    private String constructUrl(String defectId) {
        return "https://github.com/bank-of-z/vforce360/issues/" + defectId.replace("VW-", "");
    }
}
