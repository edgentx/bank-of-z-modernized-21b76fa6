package com.example.application;

import com.example.domain.vforce.model.DefectAggregate;
import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service orchestrating the defect reporting flow.
 * 1. Receives ReportDefectCmd
 * 2. Calls GitHubPort to create an issue (External Side Effect 1)
 * 3. Updates Aggregate with result
 * 4. Publishes event containing URL
 * 5. Notifies Slack (External Side Effect 2) with URL in body.
 */
@Service
public class DefectReportingService {
    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectReportingService(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public DefectReportedEvent reportDefect(ReportDefectCmd cmd) {
        log.info("Reporting defect: {}", cmd.summary());

        // 1. Create GitHub Issue
        String githubUrl;
        try {
            githubUrl = gitHubPort.createIssue(cmd.summary(), cmd.description());
            log.info("GitHub issue created: {}", githubUrl);
        } catch (Exception e) {
            log.error("Failed to create GitHub issue", e);
            githubUrl = "ERROR: Failed to create GitHub issue";
        }

        // 2. Update Aggregate (State transition)
        // In a real CQRS scenario, we'd load an existing aggregate. 
        // For reporting, we often create a new ID or use a specific one.
        // Assuming a new ID generation for this report lifecycle.
        String defectId = java.util.UUID.randomUUID().toString();
        DefectAggregate aggregate = new DefectAggregate(defectId);

        var events = aggregate.execute(new DefectAggregate.ReportDefectCommand(
            cmd.summary(), 
            cmd.description(), 
            githubUrl
        ));

        if (events.isEmpty()) {
            throw new IllegalStateException("Failed to report defect");
        }

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);

        // 3. Notify Slack (End-to-End Verification)
        try {
            slackPort.notifyDefectReported(cmd.summary(), event.githubIssueUrl());
            log.info("Slack notification sent for defect {}", defectId);
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            // Depending on policy, we might throw here or compensate.
            // For now, we log and continue to ensure the event is returned.
        }

        return event;
    }
}
