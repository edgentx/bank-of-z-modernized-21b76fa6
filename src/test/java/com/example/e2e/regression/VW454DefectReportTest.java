package com.example.e2e.regression;

import com.example.Application;
import com.example.domain.shared.Command;
import com.example.mocks.MockVForce360Port;
import com.example.ports.VForce360Port;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * 
 * Story: S-FB-1
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (Missing URL)
 * 2. Regression test added to e2e/regression/ covering this scenario
 * 
 * Expected Behavior:
 * Triggering _report_defect results in a Slack body containing the GitHub Issue URL.
 * 
 * Current State (Red Phase):
 * The actual implementation classes do not exist or are not wired.
 * This test will fail because the VForce360Port is likely not configured correctly
 * or the Service handling the logic is missing.
 */
@SpringBootTest(classes = Application.class)
class VW454DefectReportTest {

    @Autowired
    private ApplicationContext context;

    /**
     * We mock the port to verify the URL is being passed correctly downstream,
     * or we use a real Mock Adapter implementation to check the result.
     * Given the defect is about the Slack Body content (which is outside this codebase),
     * we verify the boundary: The system generates the correct GitHub URL.
     */
    @Test
    void contextLoads() {
        assertNotNull(context, "Application context should load");
    }

    /**
     * Test: Verify that the defect reporting flow captures the GitHub URL.
     * Since we are in TDD Red, we assume the necessary beans are missing.
     */
    @Test
    void shouldReportDefectAndReturnGitHubUrl() {
        // Given: A VForce360 Port is available (wired in config)
        // We expect to find a bean of type VForce360Port if the implementation exists.
        // If this throws NoSuchBeanDefinitionException, the test fails (Red phase).
        VForce360Port port = context.getBean(VForce360Port.class);
        
        // When: Reporting a defect associated with VW-454
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String title = "VW-454: Validation check for GitHub URL in Slack";
        String description = "Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)";

        String actualUrl = port.reportDefect(projectId, title, description);

        // Then: The URL should be present and match expected format (verification)
        assertNotNull(actualUrl, "The GitHub URL must not be null");
        assertTrue(actualUrl.startsWith("https://github.com/"), "URL should point to GitHub");
        assertTrue(actualUrl.contains("/issues/"), "URL should be an issue link");
    }

    /**
     * Test: Verify the specific defect fix. 
     * The user defect implies that previously the URL was missing.
     * We ensure the URL is present in the response.
     */
    @Test
    void verifyDefectSFB1_ValidatingGitHubUrlIsPresent() {
        // This test explicitly checks the requirement: "Slack body includes GitHub issue"
        // By verifying the Port returns the string.
        VForce360Port port = context.getBean(VForce360Port.class);

        String result = port.reportDefect(
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", 
            "Fix: S-FB-1", 
            "Validate URL presence"
        );

        assertFalse(result.isEmpty(), "Returned URL must not be empty");
        // Basic validation that it looks like a URL
        assertTrue(result.matches("https?://.+"), "Result must be a valid URL string");
    }
}
