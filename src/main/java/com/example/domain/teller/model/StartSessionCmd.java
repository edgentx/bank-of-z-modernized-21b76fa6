package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Assumes authentication has occurred at the gateway/tier-0 level.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated, // Verified flag from AuthZ service
        String currentNavigationState, // Current 3270 screen context
        int timeoutConfigMinutes // Configured timeout from Spring properties
) implements Command {
}