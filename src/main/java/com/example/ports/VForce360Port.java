package com.example.ports;

/**
 * Port interface for VForce360 integrations (e.g., Slack notifications).
 * Implemented by adapters using real HTTP clients, and by mocks in tests.
 */
public interface VForce360Port {

    /**
     * Reports a defect to the VForce360 system via Temporal/Slack workflow.
     *
     * @param defectTitle The title of the defect (e.g. issue ID)
     * @param githubUrl   The URL to the GitHub issue being reported
     */
    void reportDefect(String defectTitle, String githubUrl);
}
