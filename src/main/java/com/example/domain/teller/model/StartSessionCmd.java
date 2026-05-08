package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 */
public class StartSessionCmd implements Command {

    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final boolean authenticated;
    private final String context; // Operational context
    private final Instant sessionTimestamp;

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, String context, Instant sessionTimestamp) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.authenticated = authenticated;
        this.context = context;
        this.sessionTimestamp = sessionTimestamp;
    }

    public String getSessionId() { return sessionId; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public boolean isAuthenticated() { return authenticated; }
    public String getContext() { return context; }
    public Instant getSessionTimestamp() { return sessionTimestamp; }
}
