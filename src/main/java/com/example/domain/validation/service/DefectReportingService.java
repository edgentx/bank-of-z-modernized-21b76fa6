package com.example.domain.validation.service;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.ports.DefectReportGeneratorPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service handling the defect reporting workflow.
 * Orchestrates the generation of the GitHub report URL and the notification to Slack.
 * This is the implementation class targeted by the VW-454 validation test.
 */
@Service
public class DefectReportingService {

    private final DefectReportGeneratorPort reportGenerator;
    private final SlackNotificationPort slackClient;

    public DefectReportingService(DefectReportGeneratorPort reportGenerator,
                                  SlackNotificationPort slackClient) {
        this.reportGenerator = reportGenerator;
        this.slackClient = slackClient;
    }

    /**
     * Executes the defect reporting logic.
     * 1. Generates the GitHub URL via the generator port.
     * 2. Notifies Slack via the notification port.
     *
     * @param cmd The command triggering the report.
     * @return true if the workflow completed successfully.
     */
    public boolean reportDefect(ReportDefectCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        // 1. Generate the GitHub Issue URL
        String githubUrl = reportGenerator.generateDefectReportUrl(cmd);

        // 2. Construct the message body
        // CRITICAL for VW-454: The URL must be present in the body.
        String messageBody = "Defect Reported: " + githubUrl;

        // 3. Send notification
        String targetChannel = "#vforce360-issues";
        return slackClient.sendMessage(targetChannel, messageBody);
    }
}