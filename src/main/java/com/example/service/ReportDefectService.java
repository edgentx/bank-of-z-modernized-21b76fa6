package com.example.service;

import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.ports.secondary.ReportDefectPort;
import com.example.ports.secondary.SlackNotifierPort;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the Report Defect use case.
 * Orchestrates the creation of the defect report and notifies via Slack.
 */
public class ReportDefectService implements ReportDefectPort {

    private final SlackNotifierPort slackNotifier;
    private final ReconciliationBatchRepository repository;

    public ReportDefectService(SlackNotifierPort slackNotifier, ReconciliationBatchRepository repository) {
        this.slackNotifier = slackNotifier;
        this.repository = repository;
    }

    @Override
    public void reportDefect(String title, String url, String details) {
        // Construct the Slack message body
        // Defect: It was reported that this URL might be missing.
        String body = String.format(
            "Defect Detected: %s\nDetails: %s\nGitHub Issue: %s", 
            title, details, url
        );

        // Send notification asynchronously (fire and forget in this context or block)
        // For the sake of the domain logic, we trigger the port.
        CompletableFuture<String> future = slackNotifier.sendNotification(body);
        
        // In a real Temporal workflow, we might wait for this, or handle exceptions.
        // For this E2E validation, ensuring 'sendNotification' is called with the URL is key.
        future.exceptionally(ex -> {
            throw new RuntimeException("Failed to notify Slack", ex);
        });
    }
}