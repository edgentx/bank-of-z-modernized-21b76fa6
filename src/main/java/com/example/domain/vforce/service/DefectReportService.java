package com.example.domain.vforce.service;

import com.example.application.DefectReportingService;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service specific to VForce defect handling operations.
 * Wraps the generic DefectReportingService if specific VForce logic is needed.
 */
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);
    private final DefectReportingService reportingService;

    public DefectReportService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.reportingService = new DefectReportingService(gitHubPort, slackNotificationPort);
    }

    /**
     * Reports a defect.
     * 
     * Previous error: cannot find symbol method d(...)
     * Fix: Corrected the implementation to properly delegate to the reporting service.
     */
    public void reportDefect(String projectId, String title, String description, String reporter, String severity) {
        log.debug("Constructing DefectReportedEvent for project: {}", projectId);
        
        DefectReportedEvent event = new DefectReportedEvent(
            projectId,
            title,
            description,
            reporter,
            severity
        );

        reportingService.handleDefectReportedEvent(event);
    }
}
