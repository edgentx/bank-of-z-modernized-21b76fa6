package com.example.ports;

import java.util.Map;

/**
 * Interface for VForce360 integrations (Slack, Jira/GitHub issue tracking).
 * Real implementation connects to external APIs; Mocks used for testing.
 */
public interface VForce360Port {

    /**
     * Reports a defect to the tracking system.
     * Expected to return a Map containing the generated issue details (URL, ID, etc.).
     */
    Map<String, String> reportDefect(String projectId, String title, String description, String severity);
}
