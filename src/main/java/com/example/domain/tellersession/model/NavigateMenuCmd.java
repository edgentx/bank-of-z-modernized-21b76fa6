package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command for the Teller to navigate between UI screens/menus.
 * Part of the 3270 terminal emulator preservation logic.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (menuId == null || menuId.isBlank()) {
            throw new IllegalArgumentException("menuId cannot be null or blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be null or blank");
        }
    }
}