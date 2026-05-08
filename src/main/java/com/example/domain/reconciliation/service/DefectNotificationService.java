package com.example.domain.reconciliation.service;

import com.example.domain.reconciliation.model.ForceBalanceCmd;
import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.ports.DefectReporterPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Domain Service responsible for handling defect reporting workflows.
 * It orchestrates the creation of a remote issue and the subsequent Slack notification.
 */
public class DefectNotificationService {

    private static final Logger log = LoggerFactory.getLogger(DefectNotificationService.class);

    private final ReconciliationBatchRepository batchRepository;
    private final DefectReporterPort defectReporter;
    private final SlackNotificationPort slackNotification;

    public DefectNotificationService(ReconciliationBatchRepository batchRepository,
                                     DefectReporterPort defectReporter,
                                     SlackNotificationPort slackNotification) {
        this.batchRepository = batchRepository;
        this.defectReporter = defectReporter;
        this.slackNotification = slackNotification;
    }

    /**
     * Handles the defect reporting process for a failed reconciliation batch.
     * <p>
     * 1. Retrieves the failed batch details.
     * 2. Creates a GitHub issue via {@link DefectReporterPort}.
     * 3. Posts a notification to Slack containing the GitHub URL via {@link SlackNotificationPort}.
     *
     * @param command The command triggering the defect report.
     */
    public void reportDefect(ForceBalanceCmd command) {
        log.info("Processing defect report for batch: {}", command.batchId());

        ReconciliationBatch batch = batchRepository.findById(command.batchId())
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + command.batchId()));

        // Construct report details
        String title = String.format("Reconciliation Failure: Batch %s", batch.id());
        String details = String.format("Batch %s failed with reason: %s", batch.id(), batch.getReason());

        // Create remote issue (e.g., GitHub)
        String githubUrl = defectReporter.reportDefect(title, details);

        // Notify Slack with the URL (Fix for VW-454)
        String slackBody = String.format(
                "Defect detected in ReconciliationBatch: %s\nDetails: %s\nGitHub Issue: %s",
                batch.id(),
                batch.getReason(),
                githubUrl
        );

        slackNotification.postMessage("#vforce360-issues", slackBody);
        log.info("Defect report successfully posted for batch: {}", command.batchId());
    }
}
