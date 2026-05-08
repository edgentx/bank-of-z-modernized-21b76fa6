package com.vforce360.validation;

import com.vforce360.validation.core.DefectReport;
import com.vforce360.validation.core.DefectReportCommand;
import com.vforce360.validation.core.Severity;
import com.vforce360.validation.ports.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrator service handling the business logic for reporting defects.
 * Coordinates between GitHub, Slack, and Persistence layers.
 */
@Service
public class DefectReportOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(DefectReportOrchestrator.class);

    private final GitHubRestClientPort githubPort;
    private final SlackNotificationPort slackPort;
    private final ValidationRepositoryPort repositoryPort;

    public DefectReportOrchestrator(GitHubRestClientPort githubPort,
                                    SlackNotificationPort slackPort,
                                    ValidationRepositoryPort repositoryPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
        this.repositoryPort = repositoryPort;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Create Issue in GitHub.
     * 2. Persist the record.
     * 3. Notify Slack with the GitHub URL.
     *
     * @param command The defect report command.
     */
    public void executeReportDefect(DefectReportCommand command) {
        log.info("Executing defect report for: {}", command.getTitle());

        // 1. Create GitHub Issue
        CreateIssueRequest request = new CreateIssueRequest(
            command.getTitle(),
            command.getDescription(),
            command.getSeverity()
        );
        String githubUrl = githubPort.createIssue(request);

        // 2. Persist Report
        DefectReport report = new DefectReport(
            command.getTitle(),
            command.getDescription(),
            command.getSeverity(),
            githubUrl
        );
        repositoryPort.save(report);

        // 3. Notify Slack (Validates VW-454: URL in body)
        // Logic: Ensure the URL is explicitly appended to the body text.
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub issue: <%s>",
            command.getTitle(),
            command.getSeverity(),
            githubUrl
        );

        SlackMessagePayload payload = new SlackMessagePayload(slackBody);
        slackPort.sendMessage(payload);

        log.info("Defect report completed. GitHub URL: {}", githubUrl);
    }
}
