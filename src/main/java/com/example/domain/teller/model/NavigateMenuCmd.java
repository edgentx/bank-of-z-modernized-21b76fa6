package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate to a specific menu in the teller interface.
 * S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}