package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate to a specific menu or screen within the Teller UI.
 * Part of S-19: User Interface Navigation.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {

    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be null or blank");
        }
    }

    // We allow targetMenuId to be validated within the aggregate business logic
    // to enforce context rules, though the constructor ensures basic sanity.
}
