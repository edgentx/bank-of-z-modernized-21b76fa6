package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Duration;

/**
 * Command to terminate the current teller session.
 * ID: S-20
 */
public record EndSessionCmd(
        String sessionId,
        String tellerId,
        Duration timeoutThreshold
) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (timeoutThreshold == null) {
            throw new IllegalArgumentException("timeoutThreshold cannot be null");
        }
    }
}