package com.example.domain.validation;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.port.DefectRepository;
import com.example.domain.shared.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow orchestrating defect reporting, GitHub issue creation, and Slack notifications.
 */
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
        
        // 2. Call External Services (GitHub)
        String issueUrl = gitHubPort.createIssue(command.title(), command.description());
        
        // 3. Notify Slack
        // CRITICAL: Defect VW-454 states the URL must be in the body.
        String slackMessage = String.format(
            "Defect Reported: %s. GitHub Issue: %s",
            command.title(),
            issueUrl != null ? issueUrl : "FAILED_TO_CREATE"
        );
        slackNotificationPort.notify("#vforce360-issues", slackMessage);
        
        // 4. Persist Aggregate
        // Note: In a strict DDD model, we might execute the command on the aggregate 
        // and then save. Here we instantiate and save directly per the provided flow.
        // We can add the event to the aggregate here if we want to track it internally.
        defectRepository.save(aggregate);
    }
}
