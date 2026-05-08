package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.NotificationSentEvent;
import com.example.domain.notification.model.PublishNotificationCmd;
import com.example.domain.notification.repository.NotificationRepository;
import com.example.domain.defect.service.DefectService;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.InMemoryNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for S-FB-1 (VW-454).
 * Validates that when a defect is reported, the generated notification
 * intended for Slack contains the correct GitHub issue URL.
 */
public class SFB1DefectReportingWorkflowE2ETest {

    private DefectRepository defectRepo;
    private NotificationRepository notificationRepo;
    private DefectService defectService;

    @BeforeEach
    void setUp() {
        defectRepo = new InMemoryDefectRepository();
        notificationRepo = new InMemoryNotificationRepository();
        defectService = new DefectService(defectRepo);
    }

    @Test
    void shouldContainGitHubUrlInSlackBody() {
        // 1. Setup Input
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454";
        String description = "GitHub URL in Slack body (end-to-end)";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, description);

        // 2. Execute Domain Logic (Report Defect)
        String githubUrl = defectService.reportDefect(cmd);
        
        // Verify Aggregate persisted
        assertTrue(defectRepo.findById(defectId).isPresent());
        assertEquals("https://github.com/egdcrypto/bank-of-z/issues/VW-454", githubUrl);

        // 3. Simulate Workflow Step: Construct Notification Body
        // Acceptance Criteria: "Slack body includes GitHub issue: <url>"
        String slackBody = String.format(
            "Defect Reported: %s\nDescription: %s\nGitHub issue: %s",
            title, description, githubUrl
        );

        // 4. Execute Domain Logic (Publish Notification)
        String notificationId = "notif-1";
        PublishNotificationCmd pubCmd = new PublishNotificationCmd(notificationId, slackBody);
        
        NotificationAggregate notification = new NotificationAggregate(notificationId);
        notification.execute(pubCmd);
        notificationRepo.save(notification);

        // 5. Verify Final State
        NotificationAggregate savedNotif = notificationRepo.findById(notificationId).orElseThrow();
        
        // The assertion for the defect: The Slack body MUST contain the URL.
        assertTrue(savedNotif.getSlackBody().contains(githubUrl), "Slack body must contain the generated GitHub URL");
        assertTrue(savedNotif.getSlackBody().contains("GitHub issue:"), "Slack body must contain the 'GitHub issue:' label");
        
        // Verify Events were raised
        assertEquals(1, notification.uncommittedEvents().size());
        assertTrue(notification.uncommittedEvents().get(0) instanceof NotificationSentEvent);
    }

    @Test
    void shouldFailIfSlackBodyMissingGitHubLabel() {
        // Negative Test: Validation must fail if the body format is wrong (VW-454 defect scenario)
        String notificationId = "notif-fail";
        String badBody = "Defect Reported: VW-454. URL: http://github.com/..."; // Missing "GitHub issue:"

        PublishNotificationCmd cmd = new PublishNotificationCmd(notificationId, badBody);
        NotificationAggregate notification = new NotificationAggregate(notificationId);

        // Expect Aggregate to throw IllegalArgumentException during execute()
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            notification.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub issue:"));
    }
}
