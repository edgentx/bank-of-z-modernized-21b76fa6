package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the teller terminal to a specific legacy menu screen.
 * S-19: User Interface Navigation.
 */
public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action,
        boolean isAuthenticated,
        long lastActivityTimestampMillis
) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}