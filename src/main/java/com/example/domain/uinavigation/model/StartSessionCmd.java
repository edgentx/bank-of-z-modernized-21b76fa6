package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * (S-18)
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated // Indicates if the teller passed authentication checks
) implements Command {}
