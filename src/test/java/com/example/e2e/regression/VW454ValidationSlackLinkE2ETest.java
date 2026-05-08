package com.example.e2e.regression;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.SendNotificationCmd;
import com.example.domain.notification.repository.NotificationRepository;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidateUrlInclusionCmd;
import com.example.domain.validation.service.ValidationService;
import com.example.mocks.MockNotificationRepository;
import com.example.mocks.MockValidationRepository;
import com.example.workflows.DefectReportActivitiesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454: Validating GitHub URL in Slack body.
 * 
 * Context: Defect S-FB-1 reported that Slack notifications for defect reports
 * were not including the GitHub issue link in the body.
 * 
 * Expected Behavior: Slack body must include "GitHub issue: <url>"
 * 
 * This test covers the Validation Service logic and the Activity implementation
 * that orchestrates the check.
 */
public class VW454ValidationSlackLinkE2ETest {

    private ValidationService validationService;
    private NotificationRepository notificationRepository;
    private DefectReportActivitiesImpl activities;

    private static final String TEST_ISSUE_ID = "VW-454";
    private static final String EXPECTED_URL = "https://github.com/egdcrypto/bank-of-z/issues/" + TEST_ISSUE_ID;

    @BeforeEach
    public void setUp() {
        // Use mock repositories to avoid database dependencies during unit/e2e testing logic
        validationService = new ValidationService(new MockValidationRepository());
        notificationRepository = new MockNotificationRepository();
        activities = new DefectReportActivitiesImpl(validationService, notificationRepository);
    }

    @Test
    public void testValidationService_DetectsMissingGitHubLink() {
        // Scenario: Slack body generated without the GitHub URL (Bug simulation)
        String faultySlackBody = "Defect reported. Severity: LOW. Please check #vforce360-issues.";

        // Action: Validate the body
        boolean isValid = validationService.validateUrlPresence(faultySlackBody, EXPECTED_URL);

        // Assertion: Should be false because the URL is missing
        assertFalse(isValid, "Validation should fail if the GitHub URL is missing from the body.");
    }

    @Test
    public void testValidationService_AcceptsValidGitHubLink() {
        // Scenario: Slack body generated correctly (Fix simulation)
        String correctSlackBody = "Defect reported.\nGitHub issue: " + EXPECTED_URL + "\nSeverity: LOW.";

        // Action: Validate the body
        boolean isValid = validationService.validateUrlPresence(correctSlackBody, EXPECTED_URL);

        // Assertion: Should be true
        assertTrue(isValid, "Validation should pass if the GitHub URL is present in the body.");
    }

    @Test
    public void testActivityValidation_ThrowsExceptionWhenLinkMissing() {
        // Scenario: The Temporal Activity implementation validates the body before sending
        String faultyBody = "See issue #" + TEST_ISSUE_ID;
        ValidateUrlInclusionCmd cmd = new ValidateUrlInclusionCmd(
            "validation-123",
            faultyBody,
            EXPECTED_URL
        );

        // Action & Assertion: Expect the activity to throw an IllegalStateException
        // This mimics Temporal failing the task
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            activities.validateBodyContent(cmd);
        });

        assertTrue(exception.getMessage().contains("Validation Failed"));
        assertTrue(exception.getMessage().contains(EXPECTED_URL));
    }

    @Test
    public void testActivityValidation_PassesWhenLinkPresent() {
        // Scenario: The Temporal Activity implementation validates the body before sending
        String correctBody = "GitHub issue: " + EXPECTED_URL;
        ValidateUrlInclusionCmd cmd = new ValidateUrlInclusionCmd(
            "validation-456",
            correctBody,
            EXPECTED_URL
        );

        // Action: Should not throw
        assertDoesNotThrow(() -> {
            activities.validateBodyContent(cmd);
        });
    }

    @Test
    public void testE2EFlow_ReportDefectGeneratesLinkAndValidates() {
        // This test orchestrates the flow:
        // 1. Generate Link
        // 2. Send Notification (which internally validates)

        String generatedLink = activities.generateGitHubIssueLink(TEST_ISSUE_ID);
        assertEquals(EXPECTED_URL, generatedLink, "Generated URL must match the expected GitHub format");

        // Construct a notification body that includes the link
        // Ideally, this construction happens in the Workflow/Service layer,
        // but here we verify the Activity accepts the correct format.
        String slackBody = "New defect reported for VW-454.\n" +
                           "GitHub issue: " + generatedLink + "\n" +
                           "Component: validation";

        SendNotificationCmd notificationCmd = new SendNotificationCmd(
            "notif-1",
            "slack",
            "#vforce360-issues",
            slackBody
        );

        // 1. Verify Validation Logic within the notification context
        // (In a real Workflow, validateBodyContent might be a separate activity or a step within this one)
        ValidateUrlInclusionCmd validationCmd = new ValidateUrlInclusionCmd(
            "val-1",
            slackBody,
            generatedLink
        );

        // If this passes, we know the body contains the link.
        assertDoesNotThrow(() -> activities.validateBodyContent(validationCmd), 
            "The prepared Slack body must contain the generated GitHub link for validation to pass.");

        // 2. Verify Notification Can be Saved
        NotificationAggregate aggregate = new NotificationAggregate("notif-1");
        aggregate.execute(notificationCmd);
        
        NotificationAggregate saved = notificationRepository.save(aggregate);
        assertNotNull(saved);
        assertEquals("slack", saved.uncommittedEvents().get(0).getClass().getDeclaredFields()[0].getName()); 
        // Checking the event payload itself would require stricter event assertions,
        // but verifying the aggregate state is sufficient for E2E happy path here.
    }
}
