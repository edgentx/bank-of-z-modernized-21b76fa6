package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Records the intent of a user (teller) to start working on a specific terminal.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String context,    // Operational context (e.g., "MAIN_MENU")
    boolean authenticated // Authorization flag derived from Spring Security/CICS context
) implements Command {}