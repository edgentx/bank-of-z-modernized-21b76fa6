package com.example.ports;

/**
 * Port interface for the VForce360 PM diagnostic conversation integration.
 * Abstracts the external Slack/GitHub reporting mechanism.
 */
public interface VForce360Port {

    /**
     * Reports a defect to the VForce360 workflow.
     *
     * @param defectId The unique ID of the defect (e.g., VW-454).
     * @param body The message body to be sent to Slack.
     * @return true if the report was accepted, false otherwise.
     */
    boolean reportDefect(String defectId, String body);
}
