package com.example.defect.service;

import com.example.defect.SlackNotificationFormatter;
import com.example.defect.model.DefectReportedEvent;
import com.example.defect.model.ReportDefectCmd;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class DefectService {
    private final SlackNotificationFormatter formatter;

    public DefectService(SlackNotificationFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Handles the "report_defect" command triggered by the temporal worker.
     * S-FB-1: Generates the event and verifies formatting for Slack.
     */
    public String reportDefect(ReportDefectCmd cmd) {
        // Simulating GitHub Issue creation logic
        String githubIssueUrl = "https://github.com/example/issues/" + cmd.defectId();

        DefectReportedEvent event = new DefectReportedEvent(
                cmd.defectId(),
                cmd.title(),
                cmd.severity(),
                githubIssueUrl,
                Instant.now()
        );

        return formatter.format(event);
    }
}
