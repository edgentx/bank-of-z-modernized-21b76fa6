package com.example.domain.session.model;

import java.time.Instant;

/**
 * Value object or internal state representation for Teller Session details.
 * Kept separate to adhere to existing domain folder structures (Aggregate + Model files).
 */
public class TellerSession {
    
    private final String sessionId;
    private final String tellerId;
    private final Instant startedAt;
    private Instant lastActivityAt;
    private String currentScreen; // Represents the 3270 map or navigation context
    private SessionStatus status;

    public enum SessionStatus {
        ACTIVE, TERMINATED, TIMED_OUT
    }

    public TellerSession(String sessionId, String tellerId, Instant startedAt) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.startedAt = startedAt;
        this.lastActivityAt = startedAt;
        this.status = SessionStatus.ACTIVE;
        this.currentScreen = "SIGN_ON"; // Default initial screen
    }

    public void updateActivity(Instant time) {
        this.lastActivityAt = time;
    }

    public void navigate(String screenId) {
        this.currentScreen = screenId;
    }

    public void terminate() {
        this.status = SessionStatus.TERMINATED;
    }

    public void timeout() {
        this.status = SessionStatus.TIMED_OUT;
    }

    public String getSessionId() { return sessionId; }
    public String getTellerId() { return tellerId; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public String getCurrentScreen() { return currentScreen; }
    public SessionStatus getStatus() { return status; }
}
