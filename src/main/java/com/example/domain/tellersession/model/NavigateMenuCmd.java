package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate to a specific menu or screen.
 * Used in Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}
