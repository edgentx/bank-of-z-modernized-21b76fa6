package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to route the teller to a specific menu or screen.
 * Used to emulate legacy 3270 navigation flows.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        // Validation logic can be placed here or in the aggregate
        // Keeping record clean for now, validation in Aggregate
    }
}