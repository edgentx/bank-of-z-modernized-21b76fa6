package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active Teller Session.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
    }
}