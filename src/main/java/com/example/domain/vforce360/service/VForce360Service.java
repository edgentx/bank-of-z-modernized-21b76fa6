package com.example.domain.vforce360.service;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Domain Service handling the orchestration of reporting defects.
 * It coordinates the creation of a GitHub issue and the subsequent validation state update.
 */
public class VForce360Service {

    private static final Logger log = LoggerFactory.getLogger(VForce360Service.class);

    private final GithubPort githubPort;
    private final SlackNotifier slackNotifier;

    public VForce360Service(GithubPort githubPort, SlackNotifier slackNotifier) {
        this.githubPort = githubPort;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Processes a defect report.
     * 1. Creates an issue in GitHub.
     * 2. Updates the ValidationAggregate with the result.
     * 3. Sends a Slack notification.
     *
     * @param aggregate The ValidationAggregate to update.
     * @param defectTitle The title of the defect.
     * @param defectBody The description of the defect.
     * @return The URL of the created GitHub issue.
     */
    public String reportDefect(ValidationAggregate aggregate, String defectTitle, String defectBody) {
        log.info("Processing defect report for validation ID: {}", aggregate.id());

        // 1. Create GitHub Issue
        String issueUrl = githubPort.createIssue(defectTitle, defectBody);
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Update Aggregate State
        // In a real CQRS scenario, we would persist events. Here we execute logic directly for the workflow.
        // We need to construct the Command required by the Aggregate.
        // ValidationAggregate expects ReportDefectCmd.
        // We must assume the service creates the command based on the external system's response.
        com.example.domain.validation.model.ReportDefectCmd cmd = 
            new com.example.domain.validation.model.ReportDefectCmd(aggregate.id(), issueUrl);
        
        aggregate.execute(cmd);

        // 3. Notify Slack
        // The defect specifically requires the body to include the URL.
        String message = String.format("Defect Reported: <%s|GitHub Issue> created for %s", issueUrl, defectTitle);
        slackNotifier.send(message);
        log.info("Slack notification sent.");

        return issueUrl;
    }
}