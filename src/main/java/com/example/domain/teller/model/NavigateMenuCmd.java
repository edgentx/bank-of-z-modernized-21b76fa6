package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 * Part of S-19 user-interface-navigation.
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
