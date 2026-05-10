package com.example.domain.validation;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.port.DefectRepository;
import com.example.domain.shared.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

public class DefectReportingWorkflow {
    private final DefectRepository defectRepository;
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(
        DefectRepository defectRepository,
        GitHubPort gitHubPort,
        SlackNotificationPort slackNotificationPort
    ) {
        this.defectRepository = defectRepository;
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void execute(ReportDefectCommand command) {
        // 1. Create Aggregate
        DefectAggregate aggregate = new DefectAggregate(command.defectId());
        
        // 2. Call External Services
        String issueUrl = gitHubPort.createIssue(command.title(), command.description());
        
        // 3. Notify Slack
        // Note: The defect VW-454 implies this URL must be in the body.
        String slackMessage = String.format(
            "Defect Reported: %s. GitHub Issue: %s",
            command.title(),
            issueUrl != null ? issueUrl : "FAILED_TO_CREATE"
        );
        slackNotificationPort.notify("#vforce360-issues", slackMessage);
        
        // 4. Persist
        defectRepository.save(aggregate);
    }
}
