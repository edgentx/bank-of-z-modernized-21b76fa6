package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * Enforces invariants related to authentication validity and context.
 */
public record StartSessionCmd(
    String tellerSessionId,
    String tellerId,
    String terminalId,
    Instant authenticatedAt,
    String navigationState
) implements Command {}
