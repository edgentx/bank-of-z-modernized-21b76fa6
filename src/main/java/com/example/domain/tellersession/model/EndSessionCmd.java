package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 */
public class EndSessionCmd implements Command {
    private final String sessionId;

    public EndSessionCmd(String sessionId) {
        this.sessionId = sessionId;
    }

    public String sessionId() {
        return sessionId;
    }
}
