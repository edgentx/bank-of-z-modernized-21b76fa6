package com.example.domain.tellersession.model;

import java.time.Duration;
import java.time.Instant;

public class TellerSession {
    private final String sessionId;
    private final String tellerId;
    private Instant lastActivityAt;
    private final Duration timeoutDuration;
    private final boolean isAuthenticated;
    private TellerSessionState state;

    public enum TellerSessionState {
        ACTIVE, ENDED
    }

    public TellerSession(String sessionId, String tellerId, Instant lastActivityAt, Duration timeoutDuration, boolean isAuthenticated) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.lastActivityAt = lastActivityAt;
        this.timeoutDuration = timeoutDuration;
        this.isAuthenticated = isAuthenticated;
        this.state = TellerSessionState.ACTIVE;
    }

    public String sessionId() { return sessionId; }
    public String tellerId() { return tellerId; }
    public Instant lastActivityAt() { return lastActivityAt; }
    public Duration timeoutDuration() { return timeoutDuration; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public TellerSessionState state() { return state; }

    public void markEnded() {
        this.state = TellerSessionState.ENDED;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(lastActivityAt.plus(timeoutDuration));
    }

    public boolean isActive() {
        return this.state == TellerSessionState.ACTIVE;
    }
}
