package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command for navigating the Teller Terminal menus.
 * Records the target screen and the action performed (e.g., Enter, PF3).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        // Validations can be performed here if desired, or in the Aggregate.
        // For now, we rely on the Aggregate to enforce invariants.
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}