package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to route the teller to a different menu or screen.
 * Story S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt // Timestamp of when the command was issued
) implements Command {}
