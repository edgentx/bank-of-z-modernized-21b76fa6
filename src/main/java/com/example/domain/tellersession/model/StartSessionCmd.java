package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a new Teller Session.
 */
public class StartSessionCmd implements Command {

    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final Instant timeoutAt;

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, Instant timeoutAt) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.timeoutAt = timeoutAt;
    }

    public String sessionId() {
        return sessionId;
    }

    public String tellerId() {
        return tellerId;
    }

    public String terminalId() {
        return terminalId;
    }

    public Instant timeoutAt() {
        return timeoutAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartSessionCmd that = (StartSessionCmd) o;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
