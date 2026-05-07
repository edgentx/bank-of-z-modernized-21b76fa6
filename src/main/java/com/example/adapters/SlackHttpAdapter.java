package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackPort.
 * In a real scenario, this would use WebClient or RestTemplate to call Slack Webhooks.
 * For the scope of this validation fix, we verify the contract logic.
 */
@Component
public class SlackHttpAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackHttpAdapter.class);

    @Override
    public void notifyDefectReported(String defectId, String githubIssueUrl) {
        log.info("Notifying Slack channel for defect {}: GitHub Issue created at {}", defectId, githubIssueUrl);
        // Actual implementation would post to:
        // POST https://hooks.slack.com/services/...
        // Body: { "text": String.format("Defect %s filed: %s", defectId, githubIssueUrl) }
    }
}