package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Used by the UI layer (Next.js/3270 emulator) via the API.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated // Auth status determined by Spring Security / CICS gateway
) implements Command {}
