package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to route the teller to a different menu or screen based on input.
 * Emulates legacy 3270 menu navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String tellerId, // Used for internal validation
    long lastActivityTimestamp // Used for internal validation
) implements Command {}
