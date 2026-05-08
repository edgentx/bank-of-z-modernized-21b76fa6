package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * S-18 Implementation.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,   // Indicates if the teller has successfully logged in
    String currentContext,     // Navigation state (e.g., "MAIN_MENU")
    String sourceChannelId     // Channel source (e.g., "TN3270", "WEB")
) implements Command {}