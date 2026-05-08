package com.example.adapters;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Defect reporting logic invoked by Temporal workflows.
 * This class orchestrates the retrieval of the GitHub URL and the notification to Slack.
 */
@Component
public class TemporalDefectReporterImpl {

    private static final Logger log = LoggerFactory.getLogger(TemporalDefectReporterImpl.class);

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    public TemporalDefectReporterImpl(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect to the VForce360 Slack channel with the associated GitHub URL.
     * This method corresponds to the '_report_defect' temporal activity.
     *
     * @param issueId   The ID of the issue (e.g., VW-454).
     * @param channelId The target Slack channel.
     */
    public void reportDefect(String issueId, String channelId) {
        log.info("Reporting defect {} via Temporal workflow", issueId);

        String url = githubPort.getIssueUrl(issueId);
        
        // Construct the body ensuring it contains the GitHub context and URL
        String body = "Defect reported: " + url;

        slackPort.sendMessage(channelId, body);
        
        log.info("Successfully reported defect {} to channel {}", issueId, channelId);
    }
}