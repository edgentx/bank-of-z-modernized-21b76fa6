package com.example.services;

import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation of the Defect Reporting Workflow.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
@Service
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);

    private final ValidationRepository validationRepository;
    private final NotificationPort notificationPort;
    private final GitHubPort gitHubPort;

    public DefectReportingWorkflow(ValidationRepository validationRepository,
                                   NotificationPort notificationPort,
                                   GitHubPort gitHubPort) {
        this.validationRepository = validationRepository;
        this.notificationPort = notificationPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect.
     * 1. Creates a GitHub Issue.
     * 2. Sends a Slack notification containing the GitHub URL.
     *
     * @param title The defect title.
     * @param description The defect description.
     */
    public void reportDefect(String title, String description) {
        log.info("Reporting defect: {}", title);

        // 1. Create GitHub Issue
        String url = gitHubPort.createIssue(title, description);
        log.debug("GitHub issue created at: {}", url);

        // 2. Send Notification with URL in body (Fix for VW-454)
        String notificationBody = "Defect Reported: " + title + "\nGitHub Issue: " + url;
        notificationPort.sendNotification("New Defect Reported", notificationBody);

        log.info("Defect report processed.");
    }
}
