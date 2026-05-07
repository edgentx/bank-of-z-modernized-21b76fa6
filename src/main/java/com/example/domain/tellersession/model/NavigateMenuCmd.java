package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or screen.
 * Used to preserve 3270 muscle memory via web emulation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be null");
        // Action might be null or empty (e.g. default enter), but usually we want to know the intent.
        // Assuming action is required for "navigate".
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null");
    }
}
