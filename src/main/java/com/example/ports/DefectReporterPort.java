package com.example.ports;

import com.example.domain.shared.Command;
import java.util.concurrent.CompletionStage;

/**
 * Port interface for reporting defects to external systems (e.g., Slack, GitHub).
 * This decouples the domain logic from the specific implementation of Slack Webhooks or GitHub Issues API.
 */
public interface DefectReporterPort {

    /**
     * Asynchronously reports a defect.
     * @param cmd The command containing defect details.
     * @return A CompletionStage containing the report ID or URL.
     */
    CompletionStage<String> reportDefect(Command cmd);

    /**
     * Validates the format of the generated report body.
     * Used for testing requirements without actual side-effects.
     */
    String generateBodyPreview(Command cmd);
}