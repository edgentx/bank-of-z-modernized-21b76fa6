package com.example.domain.defect;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for orchestrating the defect reporting workflow.
 * Interacts with domain aggregates and external ports to fulfill the use case.
 */
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCmd by executing the domain logic and notifying external systems.
     *
     * @param cmd The command containing defect details.
     */
    public void report(ReportDefectCmd cmd) {
        // 1. Transform external DTO to internal domain command
        ReportDefectCommand domainCmd = new ReportDefectCommand(
                cmd.defectId(),
                cmd.issueId(),
                cmd.summary()
        );

        // 2. Execute domain logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(domainCmd);

        // 3. Handle side effects (Slack notification)
        if (!events.isEmpty()) {
            publishToSlack(cmd);
        }
    }

    private void publishToSlack(ReportDefectCmd cmd) {
        String issueId = cmd.issueId();
        Optional<String> urlOpt = gitHubPort.getIssueUrl(issueId);

        String slackBody;
        if (urlOpt.isPresent()) {
            String url = urlOpt.get();
            // S-FB-1: Ensure URL is present in the body
            slackBody = String.format(
                    "Defect Reported: %s%nGitHub issue: <%s|%s>",
                    cmd.summary(),
                    url,
                    issueId
            );
            log.info("Generated Slack body with URL for issue {}: {}", issueId, slackBody);
        } else {
            // Handle missing URL gracefully
            slackBody = String.format(
                    "Defect Reported: %s%nGitHub issue: %s (URL unavailable)",
                    cmd.summary(),
                    issueId
            );
            log.warn("GitHub URL not found for issue {}, sending default message.", issueId);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("text", slackBody);

        slackNotificationPort.sendMessage("#vforce360-issues", payload);
    }
}