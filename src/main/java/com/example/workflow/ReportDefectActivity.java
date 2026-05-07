package com.example.workflow;

import com.example.domain.shared.validation.ReportDefectCommand;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for defect reporting.
 * This class bridges the Temporal workflow with the domain logic and external ports.
 * In a full Temporal setup, this would be registered with the Worker.
 */
@Component
public class ReportDefectActivity {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectActivity.class);

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public ReportDefectActivity(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the defect reporting logic.
     * Corresponds to the 'report_defect' trigger mentioned in the defect reproduction steps.
     *
     * @param command The command containing defect details.
     */
    public void execute(ReportDefectCommand command) {
        log.info("Executing defect report for ID: {}", command.defectId());

        // 1. Create GitHub Issue (Simulated)
        // Note: We enrich the title with the ID for better tracking in GitHub
        String issueTitle = command.title() + " [" + command.defectId() + "]";
        String issueBody = "Defect reported via VForce360 PM diagnostic conversation.\n" +
                           "Severity: " + command.severity() + "\n" +
                           "Project: " + command.projectId();

        String issueUrl = gitHubPort.createIssue(issueTitle, issueBody);
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Post to Slack (Simulated)
        // The critical requirement for VW-454 is that the link MUST be in the Slack body.
        String slackBody = "Defect Reported: " + command.title() + "\n" +
                           "GitHub Issue: " + issueUrl;

        slackPort.postMessage("#vforce360-issues", slackBody);
        log.info("Slack notification sent to #vforce360-issues");
    }
}
