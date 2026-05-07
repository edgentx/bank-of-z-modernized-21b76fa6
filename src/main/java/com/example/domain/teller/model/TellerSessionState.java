package com.example.domain.teller.model;

/**
 * Internal state representation for the Teller Session Aggregate.
 * Encapsulates authentication status, activity tracking, and navigation context.
 */
public class TellerSessionState {
    private boolean authenticated = false;
    private Instant lastActivityAt;
    private String currentNavigationContext;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public String getCurrentNavigationContext() {
        return currentNavigationContext;
    }

    public void setCurrentNavigationContext(String currentNavigationContext) {
        this.currentNavigationContext = currentNavigationContext;
    }
}
