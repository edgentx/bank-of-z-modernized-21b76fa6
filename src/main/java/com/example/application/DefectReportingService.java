package com.example.application;

import com.example.domain.defect.DefectReportedEvent;
import com.example.domain.defect.ReportDefectCommand;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service for handling Defect Reporting.
 * Orchestrates the creation of a GitHub issue and subsequent Slack notification.
 */
@Service
public class DefectReportingService {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportingService.class);

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    public DefectReportingService(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Main entry point for Temporal Workflow/Activity or REST Controller.
     */
    public void handle(ReportDefectCommand command) {
        logger.info("Handling defect report: {}", command.aggregateId());

        // 1. Create GitHub Issue
        String githubUrl = githubPort.createIssue(command.summary(), command.description());

        // 2. Emit Domain Event (Typically would go to an EventStore, here we simulate emission)
        DefectReportedEvent event = new DefectReportedEvent(
            command.aggregateId(),
            githubUrl,
            java.time.Instant.now()
        );
        logger.info("Event emitted: {} with URL: {}", event.type(), event.getGithubUrl());

        // 3. Notify Slack
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            command.summary(),
            githubUrl
        );
        // Using channel from metadata if available, else default
        String channel = "#vforce360-issues"; 
        
        slackPort.sendNotification(channel, messageBody);
        
        logger.info("Slack notification sent to {} with URL {}", channel, githubUrl);
    }
}
