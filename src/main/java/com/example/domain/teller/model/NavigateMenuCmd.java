package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu or screen.
 * Encapsulates the legacy 3270 navigation logic (PF keys, Enter, etc.).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}