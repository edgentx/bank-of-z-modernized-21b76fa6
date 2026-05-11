package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Validates Authentication, Timeout Config, and Navigation State.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        int timeoutMinutes,
        String initialContext
) implements Command {
}
