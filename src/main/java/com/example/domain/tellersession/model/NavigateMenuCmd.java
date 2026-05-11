package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Emulates legacy 3270 menu navigation interactions (Enter, PF keys, etc.).
 */
public record NavigateMenuCmd(String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (menuId == null || menuId.isBlank()) {
            throw new IllegalArgumentException("menuId cannot be null or blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be null or blank");
        }
    }
}