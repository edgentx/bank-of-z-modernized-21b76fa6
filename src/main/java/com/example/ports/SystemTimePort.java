package com.example.ports;

import java.time.Instant;

/**
 * Port to abstract system time.
 * Critical for testing time-based invariants like Session Timeouts.
 */
public interface SystemTimePort {
    Instant now();
}
