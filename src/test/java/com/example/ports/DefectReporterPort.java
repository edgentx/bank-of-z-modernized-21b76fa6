package com.example.ports;

import java.util.Map;

/**
 * Port interface for reporting defects to external systems (e.g., Slack).
 * Placed in src/main/java as it is part of the production contract.
 */
public interface DefectReporterPort {
    void reportToSlack(Map<String, String> payload);
}
