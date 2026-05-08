package com.example.application;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity for reporting defects to GitHub and notifying Slack.
 * This implementation satisfies the VW-454 requirement to include the GitHub URL in the Slack body.
 */
@ActivityInterface
public interface DefectReportingActivity {

    Logger log = LoggerFactory.getLogger(DefectReportingActivity.class);

    /**
     * Executes the defect reporting logic.
     * Temporal Activities can return a result, which allows the Workflow to use the generated URL.
     *
     * @param command The defect report command containing ID, title, and severity.
     * @return The URL of the created GitHub issue.
     */
    @ActivityMethod
    String execute(DefectReportCommand command);

    /**
     * Implementation of the Activity.
     * We use a default method on the interface or a separate class registered with Temporal.
     * Given the test structure, we will provide the implementation logic expected by the test harness.
     */
    static class Impl implements DefectReportingActivity {
        private final GitHubIssuePort gitHubPort;
        private final SlackNotificationPort slackPort;

        public Impl(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
            this.gitHubPort = gitHubPort;
            this.slackPort = slackPort;
        }

        @Override
        public String execute(DefectReportCommand cmd) {
            log.info("Reporting defect {} via Temporal", cmd.defectId());

            // 1. Create GitHub Issue
            String issueUrl = gitHubPort.createIssue(cmd.defectId(), cmd.title());

            // 2. Notify Slack with the URL in the body (Fix for VW-454)
            String messageBody = String.format(
                "Defect Reported: %s\nSeverity: %s\nIssue: %s",
                cmd.title(),
                cmd.severity(),
                issueUrl
            );

            boolean sent = slackPort.sendMessage("#vforce360-issues", messageBody);

            if (!sent) {
                throw new RuntimeException("Failed to send Slack notification");
            }

            return issueUrl;
        }
    }
}
