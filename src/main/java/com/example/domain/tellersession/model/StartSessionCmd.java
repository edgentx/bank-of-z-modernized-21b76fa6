package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * NB: In a real system, authentication validation (checking passwords/tokens)
 * happens in the application service layer BEFORE this command is dispatched to the aggregate.
 * The aggregate verifies the PRINCIPAL (tellerId) is present and authorized contextually.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
