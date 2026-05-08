package com.example.ports;

/**
 * Port for formatting domain events into external messages (e.g., Slack blocks).
 * Implemented by Adapters in the infrastructure layer.
 */
public interface NotificationFormatterPort {
    /**
     * Formats a DefectReportedEvent into a Slack message body.
     * @param event The domain event containing defect details.
     * @return The formatted string body (JSON or Markdown).
     */
    String formatDefectForSlack(com.example.domain.reporting.model.DefectReportedEvent event);
}
