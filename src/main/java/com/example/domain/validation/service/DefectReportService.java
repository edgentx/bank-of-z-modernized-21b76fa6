package com.example.domain.validation.service;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service to handle defect reporting logic.
 * This implementation currently STUBS the functionality to satisfy the compilation/build process
 * while explicitly failing the TDD Red Phase for the actual business logic.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect via Temporal and notifies Slack.
     * RED PHASE IMPLEMENTATION: Does not perform the actual logic.
     */
    public void report(ReportDefectCmd cmd) {
        // STUB: Logic to be implemented to make tests pass.
        // Currently, this does nothing, ensuring the tests fail as required by TDD.
    }
}
