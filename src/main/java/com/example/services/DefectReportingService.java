package com.example.services;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.service.DefectService;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service orchestrating the defect reporting workflow.
 * This service coordinates between the domain logic (DefectService) and external adapters (Slack).
 * This is typically triggered by a Temporal workflow activity.
 */
@Service
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);

    private final DefectService defectService;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(DefectService defectService, SlackNotificationPort slackNotificationPort) {
        this.defectService = defectService;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Processes the command via the Domain Service.
     * 2. Notifies Slack via the Port.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        log.info("Reporting defect: {}", cmd.defectId());

        var aggregate = defectService.reportDefect(cmd);
        String gitHubUrl = aggregate.getGitHubIssueUrl();

        String message = String.format("Defect Reported: %s - %s", cmd.defectId(), gitHubUrl);
        slackNotificationPort.sendNotification(message);

        log.info("Defect {} reported successfully. Slack notification sent.", cmd.defectId());
    }
}
