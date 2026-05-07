package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.ports.GitHubIssueTracker;
import com.example.domain.defect.ports.NotificationService;

/**
 * Service to orchestrate defect reporting.
 * NOTE: This is a placeholder implementation for the RED phase of TDD.
 * It currently does nothing or throws an exception, ensuring the tests fail.
 */
public class DefectReportingService {

    private final GitHubIssueTracker gitHub;
    private final NotificationService notificationService;

    public DefectReportingService(GitHubIssueTracker gitHub, NotificationService notificationService) {
        this.gitHub = gitHub;
        this.notificationService = notificationService;
    }

    public void processDefect(ReportDefectCommand cmd) {
        // RED PHASE: Intentionally failing implementation.
        // The logic to create the GitHub issue and append the URL to the Slack body
        // is missing here.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
