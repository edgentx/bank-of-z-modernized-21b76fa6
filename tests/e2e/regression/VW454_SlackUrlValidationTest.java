package tests.e2e.regression;

import com.example.domain.validation.model.DefectAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.InMemorySlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454.
 * 
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * 
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (missing URL).
 * 2. Regression test added to e2e/regression/ covering this scenario.
 */
class VW454_SlackUrlValidationTest {

    private DefectAggregate defectAggregate;
    private InMemorySlackPort mockSlack;

    @BeforeEach
    void setUp() {
        // Using a known ID for testing consistency
        defectAggregate = new DefectAggregate("VW-454");
        mockSlack = new InMemorySlackPort();
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_whenDefectIsReported() {
        // Arrange
        String expectedGithubUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "Fix: Validating VW-454",
                "Slack body missing GitHub URL",
                expectedGithubUrl,
                Map.of("severity", "LOW", "component", "validation")
        );

        // Act
        var events = defectAggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should generate a DefectReportedEvent");
        
        // Verify the event payload
        String actualSlackBody = events.get(0).messageBody();
        
        // This is the core assertion for VW-454: The URL must be in the body.
        assertTrue(
            actualSlackBody.contains(expectedGithubUrl),
            "Slack body should contain the GitHub issue URL.\nExpected URL: " + expectedGithubUrl + "\nActual Body: " + actualSlackBody
        );
    }

    @Test
    void shouldFailValidation_ifGitHubUrlIsMissing() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "Fix: Validating VW-454",
                "Slack body missing GitHub URL",
                "", // Empty URL
                Map.of("severity", "LOW")
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> defectAggregate.execute(cmd)
        );

        assertTrue(exception.getMessage().contains("GitHub URL"));
    }

    @Test
    void shouldContainSpecificMetadataInSlackBody() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "Fix: Validating VW-454",
                "Slack body missing GitHub URL",
                "https://github.com/bank-of-z/issues/454",
                Map.of("severity", "LOW", "component", "validation")
        );

        // Act
        var events = defectAggregate.execute(cmd);
        String actualBody = events.get(0).messageBody();

        // Assert specific formatting requirements (derived from the mock logic)
        assertTrue(actualBody.contains("Severity: LOW"), "Body must contain Severity");
        assertTrue(actualBody.contains("Fix: Validating VW-454"), "Body must contain Title");
    }
}
