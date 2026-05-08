package com.example.steps;

import com.example.domain.reconciliation.model.DefectReportedEvent;
import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Regression test for Story S-FB-1.
 * Validates that when a defect is reported via Temporal worker,
 * the resulting Slack body contains the GitHub issue URL.
 */
public class SFB1_Validating_GitHub_Url_Test {

    private ReconciliationBatchRepository repository;
    private SlackNotificationPort mockSlack;

    private static final String TEST_BATCH_ID = "BATCH-454";
    private static final String GITHUB_URL = "https://github.com/bank-of-z/issues/454";

    @BeforeEach
    void setUp() {
        repository = new InMemoryReconciliationBatchRepository();
        mockSlack = mock(SlackNotificationPort.class);
    }

    @Test
    void testDefectReportedEvent_ShouldContainGitHubUrlInSlackBody() {
        // 1. Setup: Create a reconciliation batch that is out of balance
        ReconciliationBatch batch = new ReconciliationBatch(TEST_BATCH_ID);
        // Simulate the workflow triggering the defect reporting logic
        // In the real app, this happens inside the aggregate or a workflow,
        // but for domain testing, we execute the command.
        
        ReportDefectCmd cmd = new ReportDefectCmd(TEST_BATCH_ID, "Balance mismatch", GITHUB_URL);

        // 2. Execute: Report the defect
        List<DefectReportedEvent> events = batch.execute(cmd);

        // 3. Verify: The event is created
        assertFalse(events.isEmpty(), "Should generate a DefectReportedEvent");
        DefectReportedEvent event = events.get(0);

        // 4. Verify (Acceptance Criteria):
        // The validation no longer exhibits the reported behavior (missing URL)
        String slackBody = event.slackBody();
        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(slackBody.contains(GITHUB_URL), 
            "Slack body must include the GitHub issue URL. Got: " + slackBody);
        
        // Verify it's a valid looking link format
        assertTrue(slackBody.contains("<" + GITHUB_URL + ">"), 
            "Slack body should format the URL as a hyperlink <url>");
    }

    @Test
    void testDefectReportedEvent_SlackBodyStructure() {
        // Verify the full structure matches expected VForce360 format
        ReconciliationBatch batch = new ReconciliationBatch(TEST_BATCH_ID);
        ReportDefectCmd cmd = new ReportDefectCmd(TEST_BATCH_ID, "Balance mismatch", GITHUB_URL);
        
        List<DefectReportedEvent> events = batch.execute(cmd);
        DefectReportedEvent event = events.get(0);

        String body = event.slackBody();
        assertTrue(body.contains("Defect Detected"), "Missing header");
        assertTrue(body.contains(TEST_BATCH_ID), "Missing batch ID context");
    }
}
