package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or action.
 * S-19
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    // Validation can be added here if necessary, though usually done in aggregate
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}
