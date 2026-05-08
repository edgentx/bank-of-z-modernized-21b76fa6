package com.example.ports;

import com.example.domain.shared.ValidationResult;

/**
 * Port interface for reporting defects.
 * Implementations would interact with Temporal/Slack.
 */
public interface DefectReporterPort {
    void reportDefect(ValidationResult result, String githubUrl);
}
