package com.example.workflow;

import com.example.domain.shared.DefectReportedEvent;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the ReportDefectWorkflow.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
@Component
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflowImpl.class);

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    /**
     * Constructor for dependency injection.
     * Spring will automatically inject the configured adapters for these ports.
     *
     * @param githubPort The adapter for GitHub interactions.
     * @param slackPort  The adapter for Slack interactions.
     */
    public ReportDefectWorkflowImpl(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    @Override
    public void reportDefect(DefectReportedEvent event) {
        log.info("Processing defect report: {} - {}", event.defectId(), event.title());

        // 1. Create the issue in GitHub using the port
        // We combine the title and type for the description
        String description = "Defect Type: " + event.type() + "\nOccurred At: " + event.occurredAt();
        String issueUrl = githubPort.createIssue(event.title(), description);

        log.debug("GitHub issue created at: {}", issueUrl);

        // 2. Notify Slack with the URL
        // The requirement specifies that the body must include the URL.
        // We format the URL using angle brackets <url> which Slack interprets as a special link format
        // to prevent unfurling and ensure clean link text.
        String slackBody = String.format(
            "Defect Reported: %s\nGitHub Issue: <%s>",
            event.title(),
            issueUrl
        );

        slackPort.send(slackBody);

        log.info("Slack notification sent for defect: {}", event.defectId());
    }
}
