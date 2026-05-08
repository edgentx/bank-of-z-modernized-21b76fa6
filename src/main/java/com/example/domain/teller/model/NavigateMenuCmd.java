package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Part of S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    // Validation is handled by the Aggregate
}
