package com.example.application;

import com.example.domain.defect.DefectReportedEvent;
import com.example.domain.defect.ReportDefectCommand;
import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Application Service handling the defect reporting workflow.
 * Orchestrates the Aggregate execution and handles the side-effects (Slack, GitHub).
 */
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);
    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Entry point for the temporal workflow/activity.
     */
    public void handle(ReportDefectCommand cmd) {
        // 1. Execute Domain Logic
        DefectAggregate aggregate = new DefectAggregate("defect-" + cmd.defectId());
        List<DefectReportedEvent> events = aggregate.execute(cmd);

        // 2. Process Events (Side Effects)
        for (DefectReportedEvent event : events) {
            publishNotification(event);
        }
    }

    private void publishNotification(DefectReportedEvent event) {
        String slackBody = buildMessageBody(event.defectId());
        slackNotificationPort.sendMessage(event.targetChannel(), slackBody);
        log.info("Published defect report for {} to channel {}", event.defectId(), event.targetChannel());
    }

    private String buildMessageBody(String defectId) {
        Optional<String> urlOpt = gitHubIssuePort.getIssueUrl(defectId);

        if (urlOpt.isPresent()) {
            return String.format(
                    "Defect Detected: %s\nGitHub Issue: %s",
                    defectId,
                    urlOpt.get()
            );
        } else {
            return String.format(
                    "Defect Detected: %s\nGitHub Issue: URL not found (check integration)",
                    defectId
            );
        }
    }
}
