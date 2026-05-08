package com.example.domain.tellersession.model;

import java.time.Instant;

/**
 * Value object or internal state representation for TellerSession.
 * Defines SessionState and other constants used by the Aggregate.
 */
public class TellerSession {

    public enum SessionState {
        ACTIVE,
        ENDED,
        TIMEOUT
    }

    // Configured timeout period (e.g., 30 minutes)
    public static final long SESSION_TIMEOUT_MINUTES = 30;
}
