package com.example.domain.vforce;

import com.example.mocks.MockGitHub;
import com.example.mocks.MockSlackNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression test for S-FB-1.
 * Located in e2e/regression/ equivalent structure.
 * Ensures the specific defect reported in VW-454 does not resurface.
 */
class RegressionE2EValidation {

    @Test
    void regressionTest_VW454_SlackBodyContainsUrl() {
        // Setup
        MockGitHub gitHub = new MockGitHub();
        MockSlackNotification slack = new MockSlackNotification();
        DefectReporterService service = new DefectReporterService(gitHub, slack);

        // Execute scenario from defect report
        String defectTitle = "VW-454: Validating GitHub URL";
        String defectBody = "Severity: LOW";
        String targetChannel = "#vforce360-issues";

        service.reportDefect(defectTitle, defectBody, targetChannel);

        // Verify Fix
        String postedMessage = slack.getLastBodyForChannel(targetChannel);
        
        // This assertion MUST pass for the defect to be considered fixed.
        // It checks for the generic domain and the specific issue ID created by the mock.
        assertTrue(
            postedMessage.contains("https://github.com") && postedMessage.contains("issues/1"),
            "REGRESSION FAILED: Slack body does not contain the GitHub issue URL. \nBody: " + postedMessage
        );
    }
}
