package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command for a teller to navigate to a specific screen/menu.
 * Part of the 3270 muscle memory emulation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        // Validation moved to Aggregate for Domain Error enforcement, but basic sanity checks here.
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}
