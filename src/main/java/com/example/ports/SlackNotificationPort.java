package com.example.ports;

import com.example.domain.shared.Command;
import java.util.concurrent.CompletableFuture;

/**
 * Port interface for sending Slack notifications.
 * Implementations will handle the HTTP interaction with the Slack Web API.
 */
public interface SlackNotificationPort {

    /**
     * Sends a defect report to the configured Slack channel.
     *
     * @param command The command triggering the report (e.g., ReportDefectCmd)
     * @return CompletableFuture containing the message Timestamp (TS) if successful.
     */
    CompletableFuture<String> publishDefect(Command command);
}
