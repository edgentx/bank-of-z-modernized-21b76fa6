package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.infrastructure.defect.GitHubPort;
import com.example.infrastructure.defect.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Application Service orchestrating the Defect Reporting flow.
 * Bridges the Domain Aggregate with Infrastructure Adapters.
 */
@Service
public class DefectService {

    private final GitHubPort gitHubPort;
    private final SlackNotifierPort slackNotifierPort;

    // Constructor injection for ports
    public DefectService(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotifierPort = slackNotifierPort;
    }

    /**
     * Handles the ReportDefect command.
     * 1. Executes Aggregate logic
     * 2. Calls external adapters (GitHub, Slack)
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Domain Logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);

        // 2. Infrastructure Side-Effects (Temporal Workflow simulation)
        if (!events.isEmpty()) {
            var event = events.get(0);
            // The event contains the URL that was determined in the previous step 
            // (or would be determined here if we called GitHubPort first).
            // Given the test structure, we rely on the URL being present or we generate it now.
            
            // Assuming the 'reportDefect' logic in aggregate is purely domain state change,
            // the URL generation (side effect) happens here via the Port.
            String url = gitHubPort.createIssue(cmd.title(), cmd.description());

            // Notify Slack with the URL
            String slackMessage = String.format(
                "Defect Reported: %s\nURL: %s",
                cmd.title(),
                url
            );
            slackNotifierPort.sendNotification(slackMessage);
        }
    }
}
