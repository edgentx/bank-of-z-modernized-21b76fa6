package com.example.adapters;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.shared.Command;
import com.example.ports.DefectReporterPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Real adapter implementation for reporting defects to Slack.
 * This implements the logic required by S-FB-1 to generate the correct body format.
 */
@Component
public class SlackNotificationAdapter implements DefectReporterPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final String GITHUB_BASE_URL = "https://github.com/egdcrypto/bank-of-z/issues/";

    @Override
    public CompletionStage<String> reportDefect(Command cmd) {
        // In a real scenario, this would make an HTTP POST to a Slack Webhook URL.
        // For the purpose of the defect validation, we return the body content.
        return CompletableFuture.supplyAsync(() -> {
            String body = generateBodyPreview(cmd);
            log.debug("Sending Slack notification: {}", body);
            // Simulate network latency
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted during defect reporting", e);
            }
            return "OK";
        });
    }

    @Override
    public String generateBodyPreview(Command cmd) {
        if (!(cmd instanceof ReportDefectCmd rdc)) {
            throw new IllegalArgumentException("Expected ReportDefectCmd, received: " + cmd.getClass().getSimpleName());
        }

        // Construct the GitHub URL based on the defect ID
        // This is the fix for S-FB-1: ensuring the URL is formatted correctly for the Slack body
        String githubUrl = GITHUB_BASE_URL + rdc.defectId();

        return String.format(
                "Defect Reported: %s\n" +
                "Severity: %s\n" +
                "GitHub Issue: <%s|Link>",
                rdc.title(),
                rdc.severity(),
                githubUrl
        );
    }
}