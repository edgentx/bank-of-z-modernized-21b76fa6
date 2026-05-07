package com.example.domain.verification.service;

import com.example.application.DefectReportingService;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Verification service to trigger defect reporting.
 * This bridges the temporal workflow to the application service.
 */
@Service
public class VerificationService {

    private final DefectReportingService defectReportingService;
    private final SlackNotificationPort slackNotificationPort;

    public VerificationService(DefectReportingService defectReportingService,
                                SlackNotificationPort slackNotificationPort) {
        this.defectReportingService = defectReportingService;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Triggers the defect reporting flow.
     */
    public void reportDefectViaTemporal(String validationId, String description, String reporter, String severity) {
        ReportDefectCmd cmd = new ReportDefectCmd(validationId, description, reporter, severity);
        defectReportingService.reportDefect(cmd);
    }

    /**
     * Helper to notify a channel directly, used by other verification flows.
     */
    public void notifyChannel(String message) {
        slackNotificationPort.postMessage("#vforce360-issues", message);
    }
}
