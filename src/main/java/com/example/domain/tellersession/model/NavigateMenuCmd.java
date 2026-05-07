package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the teller interface to a specific menu.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action, String targetContext) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        // targetContext can be null depending on flexibility, but for this story we enforce it.
        Objects.requireNonNull(targetContext, "targetContext cannot be null");
    }
}
