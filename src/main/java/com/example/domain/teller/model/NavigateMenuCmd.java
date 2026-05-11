package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal to a specific menu or screen.
 * Emulates legacy 3270 menu navigation behavior.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        // Basic validation at the DTO level is acceptable, but Aggregate invariants are source of truth.
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
    }
}