package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the Teller Session to a specific menu or screen.
 * Part of the S-19 User Interface Navigation feature.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}
