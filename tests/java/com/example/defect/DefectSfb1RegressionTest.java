package com.example.defect;

import com.example.defect.model.DefectReportedEvent;
import com.example.defect.model.ReportDefectCmd;
import com.example.defect.service.DefectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * S-FB-1: Regression test for validating GitHub URL in Slack body.
 * Reproduction: Trigger _report_defect via temporal-worker exec -> Verify Slack body contains link.
 */
@SpringBootTest
public class DefectSfb1RegressionTest {

    @Autowired
    private SlackNotificationFormatter formatter;

    @Autowired
    private DefectService defectService;

    @Test
    public void testSlackBodyContainsGithubUrl_verifyLinkLine() {
        // 1. Setup Event simulating the temporal worker execution
        String defectId = "VW-454";
        String githubUrl = "https://github.com/example/issues/" + defectId;
        
        DefectReportedEvent event = new DefectReportedEvent(
                defectId, 
                "Validating VW-454", 
                "LOW", 
                githubUrl, 
                Instant.now()
        );

        // 2. Execute the format logic (the unit under test)
        String slackBody = formatter.format(event);

        // 3. Verify Expected Behavior: Slack body includes GitHub issue: <url>
        // We check for the presence of the URL and the markdown formatting Slack uses.
        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(slackBody.contains(githubUrl), "Slack body must contain the GitHub URL");
        assertTrue(slackBody.contains("<" + githubUrl + "|"), "Slack body must format the URL as a hyperlink");
    }

    @Test
    public void testE2E_ReportDefectCommand_generatesValidLink() {
        // E2E Scenario: Triggering the command via the service layer
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454", 
                "Defect: GitHub URL missing", 
                "Severity LOW", 
                "LOW", 
                "null pointer..."
        );

        String result = defectService.reportDefect(cmd);

        // Verify the full integration flow output
        assertNotNull(result);
        assertTrue(result.contains("https://github.com/example/issues/VW-454"), 
                "E2E check: Service must output the generated GitHub link in the response body");
    }
}
