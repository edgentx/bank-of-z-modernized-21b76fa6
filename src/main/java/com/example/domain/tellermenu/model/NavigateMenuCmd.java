package com.example.domain.tellermenu.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Legacy 3270 navigation emulation logic.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}
