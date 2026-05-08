package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Service/Workflow implementation for handling defect reporting.
 * This acts as the use case layer, orchestrating the command execution
 * and external notifications.
 */
@Component
public class DefectWorkflow {

    private final SlackNotificationPort slackNotificationPort;
    private final DefectMessageFormatter messageFormatter;

    public DefectWorkflow(SlackNotificationPort slackNotificationPort, DefectMessageFormatter messageFormatter) {
        this.slackNotificationPort = slackNotificationPort;
        this.messageFormatter = messageFormatter;
    }

    /**
     * Entry point for the _report_defect temporal-worker execution.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // The core logic to resolve the defect: formatting the body correctly
        // with the GitHub URL.
        String slackBody = messageFormatter.formatReportBody(cmd);
        
        slackNotificationPort.send("#vforce360-issues", slackBody);
    }
}
