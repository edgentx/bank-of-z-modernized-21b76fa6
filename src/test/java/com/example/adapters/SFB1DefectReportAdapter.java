package com.example.adapters;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackMessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapter responsible for the defect reporting workflow.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 * This implementation is intentionally simplistic or stubbed to act as the
 * class under test that the step definitions interact with.
 */
@Component
public class SFB1DefectReportAdapter {

    private static final Logger log = LoggerFactory.getLogger(SFB1DefectReportAdapter.class);
    private final GitHubIssuePort gitHubIssuePort;
    private final SlackMessageValidator slackMessageValidator;

    @Autowired
    public SFB1DefectReportAdapter(GitHubIssuePort gitHubIssuePort,
                                    SlackMessageValidator slackMessageValidator) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackMessageValidator = slackMessageValidator;
    }

    public void processReport(ReportDefectCmd cmd) {
        // Step 1: Create Issue
        String issueUrl = gitHubIssuePort.createIssue(cmd);

        if (issueUrl == null || issueUrl.isEmpty()) {
             throw new IllegalStateException("GitHub URL was not generated");
        }

        // Step 2: Prepare Message (VW-454 Verification)
        // We construct a message body that MUST include the URL.
        // The SlackMessageValidator implementation is primarily for sending,
        // but we construct the final string here to ensure the URL is present.
        String messageBody = String.format(
                "*Defect Reported:* %s%n*Severity:* %s%n*GitHub Issue:* %s",
                cmd.title(),
                cmd.severity(),
                issueUrl
        );

        // Step 3: Validate and Send
        // We use the validator's send method.
        slackMessageValidator.send(messageBody);
    }
}
