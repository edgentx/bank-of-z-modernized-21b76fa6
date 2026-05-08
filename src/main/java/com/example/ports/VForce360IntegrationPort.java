package com.example.ports;

/**
 * Port for communicating with external systems like Slack or GitHub.
 * Used for reporting defects from VForce360.
 */
public interface VForce360IntegrationPort {

    /**
     * Reports a defect to the issue tracker and returns the URL of the created issue.
     *
     * @param title The title of the defect.
     * @param body The body content of the defect.
     * @return The URL of the created issue.
     */
    String reportDefect(String title, String body);
}