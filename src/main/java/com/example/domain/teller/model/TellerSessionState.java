package com.example.domain.teller.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Internal state representation for the Teller Session.
 * Tracks authentication, activity, and navigation context.
 */
public class TellerSessionState {
    private boolean isAuthenticated;
    private Instant lastActivityAt;
    private String navigationContext; // e.g., "MAIN_MENU", "IDLE"

    public TellerSessionState(boolean authenticated, Instant lastActivity, String navContext) {
        this.isAuthenticated = authenticated;
        this.lastActivityAt = lastActivity;
        this.navigationContext = navContext;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public String getNavigationContext() {
        return navigationContext;
    }

    /**
     * Check if session has timed out based on configured inactivity period.
     * Assuming 15 minutes timeout for bank terminals.
     */
    public boolean isTimedOut(Instant now) {
        if (lastActivityAt == null) return true;
        long minutesElapsed = ChronoUnit.MINUTES.between(lastActivityAt, now);
        return minutesElapsed > 15;
    }
}
