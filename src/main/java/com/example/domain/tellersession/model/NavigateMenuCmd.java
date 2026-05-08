package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the teller UI to a new menu or screen.
 * S-19: User-Interface-Navigation
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        Objects.requireNonNull(targetMenuId, "targetMenuId required");
        if (targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId required");
        Objects.requireNonNull(action, "action required");
    }
}
