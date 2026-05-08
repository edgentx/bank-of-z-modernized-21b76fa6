package com.example.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class SessionStartedEvent {
    private final UUID sessionId;
    private final String tellerId;
    private final String terminalId;
    private final Instant startedAt;
    private final Duration timeoutDuration;

    public SessionStartedEvent(UUID sessionId, String tellerId, String terminalId, Instant startedAt, Duration timeoutDuration) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.startedAt = startedAt;
        this.timeoutDuration = timeoutDuration;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getTellerId() {
        return tellerId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Duration getTimeoutDuration() {
        return timeoutDuration;
    }
}
