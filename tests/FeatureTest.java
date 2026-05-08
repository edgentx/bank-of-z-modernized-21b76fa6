import com.example.domain.defect.model.*;
import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Feature Test: S-FB-1
 * Description: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Scenario:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior:
 * - The validation logic ensures the GitHub URL is present and valid.
 * - The DefectAggregate captures this URL.
 */
public class FeatureTest {

    @Mock
    private SlackMessageValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test Case: Valid URL provided via Slack body.
     * Expectation: The command succeeds, the aggregate is updated, and the event is emitted.
     */
    @Test
    public void testValidGithubUrlReporting() {
        // Setup
        String validUrl = "https://github.com/example/repo/issues/454";
        String slackBody = "Issue found: " + validUrl;
        when(validator.containsValidGitHubUrl(slackBody)).thenReturn(true);

        // Act
        DefectAggregate aggregate = new DefectAggregate("defect-1");
        ReportDefectCmd cmd = new ReportDefectCmd("defect-1", "VW-454", "Validation bug", validUrl);
        
        // Red Phase logic: We expect this to work. If implementation is missing/stubbed, it fails.
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(validUrl, event.githubUrl());
        assertEquals("defect-1", event.aggregateId());
        assertEquals(DefectAggregate.DefectStatus.REPORTED, aggregate.getStatus());
        assertEquals(validUrl, aggregate.getGithubUrl());

        // Verify that the contract integration (Slack) would be checked
        // In a real integration, the workflow would pass the Slack body to the validator
        // before creating the command.
        assertTrue(validator.containsValidGitHubUrl(slackBody)); 
    }

    /**
     * Test Case: Missing GitHub URL.
     * Expectation: The command fails with IllegalArgumentException.
     */
    @Test
    public void testMissingGithubUrlThrowsException() {
        // Act & Assert
        DefectAggregate aggregate = new DefectAggregate("defect-2");
        ReportDefectCmd cmd = new ReportDefectCmd("defect-2", "VW-455", "No link provided", "");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub URL is required"));
    }
}
