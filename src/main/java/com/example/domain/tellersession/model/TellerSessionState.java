package com.example.domain.tellersession.model;

/**
 * Internal state enumeration for TellerSession.
 * Used to enforce invariants regarding timeouts and navigation.
 */
public enum TellerSessionState {
    NONE,            // Initial state
    TIMED_OUT,       // Indicates violation: Session timed out
    INVALID_NAVIGATION // Indicates violation: Bad navigation context
}
