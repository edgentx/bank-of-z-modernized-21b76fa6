package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for notifying external systems (e.g. Slack) about domain events.
 */
public interface SlackNotifier {
    void notifyDefectReported(String aggregateId, String githubIssueUrl);
}
