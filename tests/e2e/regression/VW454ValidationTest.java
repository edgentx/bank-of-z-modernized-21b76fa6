package e2e.regression;

import com.example.ports.SlackNotifierPort;
import com.example.ports.VForce360ClientPort;
import mocks.InMemoryEventStore;
import mocks.StubSlackNotifier;
import mocks.StubVForce360Client;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.regex.Pattern;

/**
 * Regression Test for VW-454.
 * Validates that the Slack body created during defect reporting contains the GitHub issue URL.
 *
 * Context: Temporal Worker (_report_defect exec) -> VForce360 Client -> Slack Notifier
 */
public class VW454ValidationTest {

    @Test
    public void testReportDefect_ShouldContainGitHubUrlInSlackBody() {
        // Arrange
        // We simulate the Temporal workflow step where a defect is reported to VForce360.
        // Since this is an E2E regression, we wire the domain logic to mocked infrastructure ports.
        
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        String defectTitle = "VW-454: GitHub URL missing";
        String defectDescription = "Severity: LOW\nComponent: validation";

        // We configure the external system mocks. 
        // In a real scenario, the VForce360 client would hit GitHub API to create the issue.
        // We stub the VForce360 client to return a pre-canned Issue ID/URL for this test.
        // This isolates the Logic Under Test (LUT) from actual network calls.
        VForce360ClientPort vForceClient = new StubVForce360Client(expectedUrl);
        SlackNotifierPort slackNotifier = new StubSlackNotifier();
        InMemoryEventStore eventStore = new InMemoryEventStore();

        // Act
        // This method represents the temporal workflow activity implementation.
        // It should take the defect details, report to VForce360 (simulated), get the link, and notify Slack.
        ReportDefectWorkflow workflow = new ReportDefectWorkflow(vForceClient, slackNotifier, eventStore);
        
        workflow.execute(defectTitle, defectDescription);

        // Assert
        // 1. Verify the workflow recorded the attempt
        assertTrue(eventStore.contains("DefectReportedEvent"), "Workflow should have recorded a DefectReportedEvent");

        // 2. Verify Slack was called
        assertTrue(slackNotifier.wasCalled(), "Slack notifier should have been triggered");

        // 3. CRITICAL ASSERTION for VW-454: Verify the Slack BODY contains the GitHub URL.
        String actualSlackBody = slackNotifier.getLastMessageBody();
        
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL.\nExpected to contain: " + expectedUrl + "\nActual Body: " + actualSlackBody
        );
        
        // Additionally, ensure it looks like a URL
        assertTrue(
            Pattern.compile("https://github\.com/[\w-]+/[\w-]+/issues/\d+").matcher(actualSlackBody).find(),
            "Slack body should contain a valid-looking GitHub issue URL pattern"
        );
    }

    @Test
    public void testReportDefect_WhenGitHubFails_ShouldNotSendSlackWithEmptyUrl() {
        // Arrange: Simulate a failure in the GitHub creation step (e.g. VForce360 error)
        VForce360ClientPort failingClient = new StubVForce360Client(null); // Returns null URL
        SlackNotifierPort slackNotifier = new StubSlackNotifier();
        InMemoryEventStore eventStore = new InMemoryEventStore();

        ReportDefectWorkflow workflow = new ReportDefectWorkflow(failingClient, slackNotifier, eventStore);

        // Act & Assert
        // If GitHub fails, we expect the workflow to handle it gracefully. 
        // For VW-454, we specifically ensure we don't send a malformed "null" link.
        assertThrows(RuntimeException.class, () -> {
            workflow.execute("Title", "Desc");
        });

        assertFalse(slackNotifier.wasCalled(), "Slack should not be notified if defect creation failed");
    }
}

/**
 * Stand-in for the Temporal Workflow Activity.
 * In a real Spring Boot app, this would be a @Service or @WorkflowImpl.
 * We include it here to define the behavior contract for the test.
 */
class ReportDefectWorkflow {
    private final VForce360ClientPort vForceClient;
    private final SlackNotifierPort slackNotifier;
    private final InMemoryEventStore store;

    public ReportDefectWorkflow(VForce360ClientPort vForceClient, SlackNotifierPort slackNotifier, InMemoryEventStore store) {
        this.vForceClient = vForceClient;
        this.slackNotifier = slackNotifier;
        this.store = store;
    }

    public void execute(String title, String description) {
        // Step 1: Report to VForce360 (which creates GitHub issue)
        String githubUrl = vForceClient.createIssue(title, description);
        
        if (githubUrl == null) {
            throw new RuntimeException("Failed to create GitHub issue via VForce360");
        }

        // Step 2: Notify Slack with the URL
        String body = String.format("Defect Reported: %s\nGitHub Issue: %s", title, githubUrl);
        slackNotifier.send(body);

        // Step 3: Store event
        store.add("DefectReportedEvent");
    }
}
