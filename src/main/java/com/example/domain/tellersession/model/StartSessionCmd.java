package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Internal command used to setup the aggregate state for testing.
 * Mirrors the Initiate pattern seen in other aggregates.
 */
public record StartSessionCmd(
    String sessionId, 
    String tellerId, 
    String terminalId, 
    Instant startedAt, 
    Instant validUntil
) implements Command {}
