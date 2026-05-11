package com.example.domain.validation;

import com.example.config.TestConfiguration;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = TestConfiguration.class)
class DefectReportingWorkflowTest {

    @Autowired
    private DefectReportingWorkflow workflow;

    @Autowired
    private MockGitHubPort mockGitHubPort;

    @Autowired
    private MockSlackPort mockSlackPort;

    @Test
    void testReportDefect_generatesGitHubLink_andIncludesItInSlackBody() {
        // Arrange
        String defectId = "FB-454";
        String description = "Fix: Validating VW-454 — GitHub URL in Slack body";
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        
        mockGitHubPort.setMockUrl(expectedUrl);
        ReportDefectCommand command = new ReportDefectCommand(defectId, description, null);

        // Act
        String resultUrl = workflow.reportDefect(command);

        // Assert
        assertEquals(expectedUrl, resultUrl, "Workflow should return the GitHub URL");
        
        // Verify Behavior (The Defect Fix)
        String slackBody = mockSlackPort.getLastSentBody();
        assertNotNull(slackBody, "Slack should have been called");
        assertTrue(slackBody.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL. Found: " + slackBody);
    }
}