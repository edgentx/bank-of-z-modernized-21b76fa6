package com.example.application;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Repository;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class DefectReportingService {

    private final VForce360Repository repository;
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(VForce360Repository repository,
                                  GitHubPort gitHubPort,
                                  SlackNotificationPort slackNotificationPort) {
        this.repository = repository;
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        // Create Aggregate
        // Using a simple UUID generation for ID as ReportDefectCmd doesn't inherently have one
        String defectId = java.util.UUID.randomUUID().toString();
        VForce360Aggregate aggregate = new VForce360Aggregate(defectId);

        // Execute Command
        aggregate.execute(cmd);

        // Call GitHub Adapter
        String issueUrl = gitHubPort.createIssue(cmd.title(), cmd.body(), Map.of(
            "project", cmd.project(),
            "severity", cmd.severity()
        ));

        // Call Slack Adapter with URL in body
        // This satisfies VW-454: URL must be in the Slack body
        String slackMessage = String.format(
            "Defect Reported: %s\nGitHub Issue URL: %s",
            cmd.title(),
            issueUrl
        );
        slackNotificationPort.sendNotification(slackMessage);

        // Save Aggregate
        repository.save(aggregate);
    }
}
