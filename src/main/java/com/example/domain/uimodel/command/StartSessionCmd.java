package com.example.domain.uimodel.command;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public class StartSessionCmd implements Command {
    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final boolean authenticated;
    private final NavContext context;
    private final Instant requestedAt;
    private final long timeoutSeconds;

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, NavContext context, Instant requestedAt, long timeoutSeconds) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.authenticated = authenticated;
        this.context = context;
        this.requestedAt = requestedAt;
        this.timeoutSeconds = timeoutSeconds;
    }

    public String sessionId() { return sessionId; }
    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
    public boolean isAuthenticated() { return authenticated; }
    public NavContext getContext() { return context; }
    public Instant getRequestedAt() { return requestedAt; }
    public long getTimeoutSeconds() { return timeoutSeconds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartSessionCmd that = (StartSessionCmd) o;
        return authenticated == that.authenticated && timeoutSeconds == that.timeoutSeconds && Objects.equals(sessionId, that.sessionId) && Objects.equals(tellerId, that.tellerId) && Objects.equals(terminalId, that.terminalId) && Objects.equals(context, that.context) && Objects.equals(requestedAt, that.requestedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, tellerId, terminalId, authenticated, context, requestedAt, timeoutSeconds);
    }
}