package com.example.e2e.regression;

import com.example.adapters.GitHubPort;
import com.example.adapters.SlackPort;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.services.DefectReportService;
import com.example.e2e.mocks.MockGitHubAdapter;
import com.example.e2e.mocks.MockSlackAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * End-to-end regression test for VW-454.
 * Validates that the GitHub URL created in the external system is correctly
 * propagated into the Slack message body.
 */
@SpringBootTest
@ContextConfiguration(classes = VW454SlackUrlValidationE2ETest.TestConfig.class)
class VW454SlackUrlValidationE2ETest {

    @Autowired
    private DefectReportService defectReportService;

    @Autowired
    private MockSlackAdapter mockSlackAdapter;

    @Autowired
    private MockGitHubAdapter mockGitHubAdapter;

    @BeforeEach
    void resetMocks() {
        mockSlackAdapter.reset();
        mockGitHubAdapter.reset();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHubAdapter.setMockUrl(expectedGitHubUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "vw-454",
            "VW-454 — GitHub URL in Slack body",
            "Validating that the URL is present",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // Act (Trigger temporal-worker exec equivalent)
        defectReportService.handleReportDefect(cmd);

        // Assert
        // 1. Verify GitHub creation was attempted
        assertTrue(mockGitHubAdapter.wasCreateCalled());

        // 2. Verify Slack message was sent
        assertTrue(mockSlackAdapter.wasMessageSent());

        // 3. Validate the CRITICAL Acceptance Criteria: URL is in the body
        String actualSlackBody = mockSlackAdapter.getCapturedBody();
        assertNotNull(actualSlackBody, "Slack body should not be null");
        
        // The defect specifically checks for the presence of the link line
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body must include the GitHub issue URL. Expected to contain: " + expectedGitHubUrl + " but got: " + actualSlackBody
        );
    }

    @Configuration
    @Import({DefectReportService.class})
    static class TestConfig {
        @Bean
        public GitHubPort gitHubPort() {
            return new MockGitHubAdapter();
        }

        @Bean
        public SlackPort slackPort() {
            return new MockSlackAdapter();
        }
    }
}
