package com.example.workflow;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.model.SlackNotificationWithUrlCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

/**
 * Orchestrator (Service/Activity) handling the defect reporting workflow.
 * <p>
 * This class implements the fix for VW-454.
 * It coordinates creating the GitHub issue and then notifying Slack with the URL.
 */
@Component
public class DefectReportingOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingOrchestrator.class);

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingOrchestrator(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     *
     * @param cmd The command containing defect details.
     */
    public void execute(Command cmd) {
        if (!(cmd instanceof ReportDefectCmd defectCmd)) {
            throw new IllegalArgumentException("Expected ReportDefectCmd, received: " + cmd.getClass().getSimpleName());
        }

        try {
            // 1. Create GitHub Issue
            String issueUrl = gitHubIssuePort.createIssue(
                    "Defect: " + defectCmd.defectId(),
                    defectCmd.description()
            ).join(); // Synchronous join for the orchestrator logic

            log.info("GitHub issue created: {}", issueUrl);

            // 2. Publish to Slack with URL in body
            // We wrap the URL and details into a new Command for the Slack adapter
            SlackNotificationWithUrlCmd slackCmd = new SlackNotificationWithUrlCmd(
                    defectCmd.defectId(),
                    issueUrl,
                    defectCmd.severity()
            );

            slackNotificationPort.publishDefect(slackCmd).join();

            log.info("Slack notification sent for defect {}", defectCmd.defectId());

        } catch (CompletionException e) {
            log.error("Failed to report defect {}", defectCmd.defectId(), e);
            throw new RuntimeException("Defect reporting failed for " + defectCmd.defectId(), e.getCause());
        }
    }
}
