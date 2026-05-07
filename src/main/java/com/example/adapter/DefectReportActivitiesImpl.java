package com.example.adapter;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;

/**
 * Implementation of Temporal Activities for Defect Reporting.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class DefectReportActivitiesImpl {

    private static final Logger log = LoggerFactory.getLogger(DefectReportActivitiesImpl.class);

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;
    private final ObservationRegistry observationRegistry;

    public DefectReportActivitiesImpl(SlackPort slackPort, 
                                      GitHubPort gitHubPort, 
                                      ObservationRegistry observationRegistry) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
        this.observationRegistry = observationRegistry;
    }

    /**
     * Reports a defect by creating a GitHub issue and sending a Slack notification.
     * 
     * @param title The defect title.
     * @param body  The defect description.
     */
    public void reportDefect(String title, String body) {
        Observation observation = Observation.createNotStarted("defect.report", observationRegistry)
                .lowCardinalityKeyValue("action", "report");
        
        observation.observe(() -> {
            try {
                // 1. Create GitHub Issue
                // For this defect fix (S-FB-1), we must capture the returned URL.
                URI issueUrl = gitHubPort.createIssue(title, body)
                        .orElseThrow(() -> new IllegalStateException("Failed to create GitHub issue for defect: " + title));

                // 2. Construct Slack Message including the GitHub URL
                // The previous bug was likely that the URL was omitted from this message.
                String slackMessage = String.format(
                    "Defect Reported: %s\nGitHub Issue: %s",
                    title, 
                    issueUrl.toString()
                );

                log.info("Sending notification to Slack for issue: {}", issueUrl);
                
                // 3. Send Notification
                slackPort.sendMessage(slackMessage);
                
            } catch (Exception e) {
                log.error("Error reporting defect {}", title, e);
                // Depending on workflow requirements, we might want to throw here to fail the workflow,
                // or try to notify Slack of the failure. For now, we propagate the exception.
                throw new RuntimeException("Failed to report defect", e);
            }
        });
    }
}
