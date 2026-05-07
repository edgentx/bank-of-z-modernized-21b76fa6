package com.example.domain.verification.service;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Verification Service to check integration points (VForce360 diagnostic).
 */
public class VerificationService {

    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public VerificationService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Triggers a diagnostic check.
     * Previous error: incompatible types: java.lang.String cannot be converted to java.util.Map
     * Fix: Pass Map.of() instead of a String label.
     * Previous error: method postMessage cannot be applied to given types (String, String)
     * Fix: Pass a single formatted String.
     */
    public void verifyVW454() {
        log.info("Verifying VW-454: GitHub URL in Slack body");

        // 1. Create a test issue
        String url = gitHubPort.createIssue(
            "VW-454 Verification",
            "Verification run for VW-454 defect.",
            Map.of("type", "diagnostic")
        );

        // 2. Post verification result to Slack
        String message = String.format("Verification VW-454 completed. Issue created at: %s", url);
        slackNotificationPort.postMessage(message);
    }
}
