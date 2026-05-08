package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a teller session.
 * S-18: user-interface-navigation.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant authenticatedAt // Represents successful auth
) implements Command {}
