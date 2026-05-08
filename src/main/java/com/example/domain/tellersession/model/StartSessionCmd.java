package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

import java.util.Objects;

/**
 * Command to initiate a teller session.
 */
public class StartSessionCmd implements Command {

    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final boolean authenticated;
    private final Instant sessionTimeoutAt;
    private final String initialContext;

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, Instant sessionTimeoutAt, String initialContext) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.authenticated = authenticated;
        this.sessionTimeoutAt = sessionTimeoutAt;
        this.initialContext = initialContext;
    }

    public String getSessionId() { return sessionId; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public boolean isAuthenticated() { return authenticated; }
    public Instant getSessionTimeoutAt() { return sessionTimeoutAt; }
    public String getInitialContext() { return initialContext; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartSessionCmd that = (StartSessionCmd) o;
        return authenticated == that.authenticated && Objects.equals(sessionId, that.sessionId) && Objects.equals(tellerId, that.tellerId) && Objects.equals(terminalId, that.terminalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, tellerId, terminalId, authenticated);
    }
}
