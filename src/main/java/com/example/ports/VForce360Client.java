package com.example.ports;

/**
 * Port for communicating with the external VForce360 PM diagnostic service.
 * This interface abstracts the Temporal/activity or HTTP call used to report a defect.
 */
public interface VForce360Client {

    /**
     * Reports a defect to the VForce360 system.
     * This is expected to trigger a Slack notification.
     *
     * @param defectTitle The title of the defect (e.g., "Validating VW-454")
     * @param projectId   The ID of the project component
     * @param severity    The severity level
     * @return The body of the message sent (e.g., Slack payload).
     */
    String reportDefect(String defectTitle, String projectId, String severity);
}
