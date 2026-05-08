package com.example.e2e.regression;

import com.example.Application;
import com.example.mocks.MockVForce360Port;
import com.example.ports.VForce360Port;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1.
 * Validates that the defect reporting workflow includes the GitHub URL in the Slack body.
 * 
 * Context: VForce360 PM diagnostic workflow integration.
 */
@SpringBootTest(classes = SFB1DefectValidationIT.TestConfig.class)
public class SFB1DefectValidationIT {

    @Autowired
    private MockVForce360Port mockVForce360;

    @Autowired
    private VForce360ServiceGateway serviceGateway; // The class under test

    /**
     * Validates VW-454: GitHub URL in Slack body.
     * 
     * Given: A defect 'VW-454' is triggered via the temporal-worker exec
     * When: The report_defect workflow executes
     * Then: The Slack body includes the GitHub issue URL
     */
    @Test
    void testSFB1_ShouldContainGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String defectDescription = "Defect: Validating VW-454";
        
        // Act - Trigger the workflow via the Gateway (Temporal entry point)
        // Note: This implementation does not exist yet, so the test will fail.
        serviceGateway.reportDefectViaTemporal(defectId, defectDescription);

        // Assert - Verify the external port received the correct payload
        assertEquals(1, mockVForce360.getCapturedReports().size(), "Should have triggered one report");

        MockVForce360Port.Report report = mockVForce360.getCapturedReports().get(0);
        assertEquals(defectId, report.defectId(), "Defect ID must match");

        String slackBody = report.body();
        assertNotNull(slackBody, "Slack body must not be null");
        
        // CRITICAL CHECK: Validate presence of GitHub URL pattern
        // Expected format: "GitHub issue: <http...>"
        assertTrue(
            slackBody.contains("GitHub issue:"), 
            "Slack body must contain 'GitHub issue:' label."
        );
        
        assertTrue(
            slackBody.contains("http"), 
            "Slack body must contain a URL (http/https)."
        );
    }

    /**
     * Test Configuration for the Spring Boot Test Context.
     * Uses the Mock implementation instead of the real HTTP client/Slack API.
     */
    @Configuration
    @Import(Application.class)
    static class TestConfig {
        
        @Bean
        public VForce360Port vForce360Port() {
            return new MockVForce360Port();
        }

        @Bean
        public VForce360ServiceGateway vForce360ServiceGateway(VForce360Port port) {
            return new VForce360ServiceGateway(port);
        }
    }

    /**
     * Gateway class representing the Temporal Worker / Orchestrator entry point.
     * This class is expected to exist in the main codebase to handle the workflow.
     * We are defining it here only to satisfy the test wiring, but the implementation
     * will be missing causing the compilation/execution failure.
     */
    public static class VForce360ServiceGateway {
        private final VForce360Port port;

        public VForce360ServiceGateway(VForce360Port port) {
            this.port = port;
        }

        public void reportDefectViaTemporal(String defectId, String description) {
            // In the real implementation, this would construct the payload and call port.reportDefect.
            // Since we are in RED phase, this logic is missing or stubbed out incorrectly.
            throw new UnsupportedOperationException("STORY S-FB-1: Temporal worker logic not implemented yet.");
        }
    }
}
