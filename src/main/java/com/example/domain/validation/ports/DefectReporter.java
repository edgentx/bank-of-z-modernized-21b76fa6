package com.example.domain.validation.ports;

/**
 * Port for reporting defects to external systems.
 * Implementations will handle Slack, Temporal, and GitHub interactions.
 */
public interface DefectReporter {
    void reportDefect(String title, String description);
}