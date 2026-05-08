package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 */
public class StartSessionCmd implements Command {

    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final boolean authenticated;
    private final boolean stale;
    private final String context; // Operational context / Navigation state

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, boolean stale, String context) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.authenticated = authenticated;
        this.stale = stale;
        this.context = context;
    }

    public String getSessionId() { return sessionId; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public boolean isAuthenticated() { return authenticated; }
    public boolean isStale() { return stale; }
    public String getContext() { return context; }
}
