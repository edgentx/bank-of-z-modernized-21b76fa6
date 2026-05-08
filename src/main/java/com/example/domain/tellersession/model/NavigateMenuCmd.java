package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or screen.
 * Part of the user-interface-navigation aggregate.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    // Validation is delegated to the Aggregate execute method to ensure all invariants are checked atomically.
}
