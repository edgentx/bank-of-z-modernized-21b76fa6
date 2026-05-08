package com.example.e2e;

import com.example.application.DefectReportService;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.infrastructure.TemporalActivitiesImpl;
import com.example.domain.shared.SlackMessageValidator;
import io.temporal.workflow.Workflow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * E2E Regression Test for VW-454.
 * Validates that triggering the defect report workflow results in a Slack message
 * containing the GitHub issue URL.
 */
@SpringBootTest
class DefectReportWorkflowE2ETest {

    @Autowired
    private DefectReportService defectReportService;

    // Mock the Temporal Activity implementation to prevent actual external calls
    @MockBean
    private TemporalActivitiesImpl temporalActivities;

    // Mock the Validator to allow inspection or ensure it's called correctly
    @MockBean
    private SlackMessageValidator slackMessageValidator;

    @Test
    void workflow_should_generate_slack_body_with_github_url() {
        // Arrange
        String defectId = "vw-454";
        String title = "GitHub URL in Slack body";
        String severity = "LOW";
        String expectedGithubUrl = "https://github.com/bank-of-z/modernized/issues/454";

        // We assume the Workflow constructs the URL or we pass it.
        // For this test, we verify the integration wiring.
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, severity);

        // Configure mock validator to pass (or throw if we want to test failure handling)
        doNothing().when(slackMessageValidator).validate(anyString(), anyString());

        // Act
        // Trigger the service. In a real Temporal test, we'd use TestWorkflowEnvironment.
        // Here we assume the Service triggers the workflow synchronously or we test the logic chain.
        defectReportService.reportDefect(cmd);

        // Assert
        // Verify that the validator was called with the expected URL present in the body.
        // This captures the regression requirement: "Slack body includes GitHub issue".
        verify(slackMessageValidator).validate(contains(expectedGithubUrl), eq(expectedGithubUrl));
        verify(temporalActivities).sendSlackNotification(anyString()); // Ensure Slack was triggered
    }

    private String contains(String expected) {
        return argThat(arg -> arg != null && arg.contains(expected));
    }
}
