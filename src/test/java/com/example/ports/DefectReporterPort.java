package com.example.ports;

import java.util.Map;

/**
 * Port interface for reporting defects to external systems (e.g., Slack).
 * Placed in src/test/java for isolation as per instructions, though typically
 * this would live in src/main/java.
 */
public interface DefectReporterPort {
    void reportToSlack(Map<String, String> payload);
}
