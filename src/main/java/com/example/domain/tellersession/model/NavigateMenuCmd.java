package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the teller to a specific menu or screen.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null or empty");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be null or empty");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null or empty");
    }
}
