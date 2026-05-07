package com.example.ports;

/**
 * Port interface for reporting defects.
 * Used by the Temporal Worker logic to decouple from specific implementations.
 */
public interface DefectReporterPort {

    /**
     * Reports a defect to the specified channel, including a formatted link.
     *
     * @param channelId The target Slack channel ID.
     * @param url The GitHub issue URL to include.
     */
    void reportDefect(String channelId, String url);
}
