package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * Story S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    Instant authenticationTime,
    String operationalContext
) implements Command {}
