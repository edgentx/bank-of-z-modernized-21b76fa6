package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu or screen.
 * Part of Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId required");
        // Action might be optional in some contexts, but S-19 requires it for valid command scenarios
        // if (action == null) throw new IllegalArgumentException("action required");
    }
}