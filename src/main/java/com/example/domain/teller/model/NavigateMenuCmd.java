package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller to a different menu or screen.
 * Used in S-19: TellerSession user-interface-navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}
