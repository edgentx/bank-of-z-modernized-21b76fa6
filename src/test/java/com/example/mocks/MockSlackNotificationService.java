package com.example.mocks;

import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import com.example.ports.DefectReporterPort;
import com.example.domain.shared.Command;
import com.example.domain.shared.ReportDefectCmd;

/**
 * Mock implementation of DefectReporterPort for Slack.
 * Simulates the generation of a Slack message body containing a GitHub link.
 * This is the 'system under test' for the validation logic described in S-FB-1.
 */
@Component
public class MockSlackNotificationService implements DefectReporterPort {

    @Override
    public CompletionStage<String> reportDefect(Command cmd) {
        // Simulate async processing
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(50); // Simulate network latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return generateBodyPreview(cmd);
        });
    }

    @Override
    public String generateBodyPreview(Command cmd) {
        if (!(cmd instanceof ReportDefectCmd rdc)) {
            throw new IllegalArgumentException("Expected ReportDefectCmd");
        }

        // Construct the expected GitHub URL based on the defect ID
        // Corresponds to the 'GitHub issue link' expected behavior
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/" + rdc.defectId();

        // Construct the Slack Body
        // This is the string we will validate against in the test
        return String.format(
                "Defect Reported: %s\n" +
                "Severity: %s\n" +
                "GitHub Issue: <%s|Link>", // Slack link format
                rdc.title(),
                rdc.severity(),
                githubUrl
        );
    }
}
