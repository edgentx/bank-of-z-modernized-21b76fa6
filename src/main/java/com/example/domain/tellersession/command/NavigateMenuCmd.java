package com.example.domain.tellersession.command;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal UI to a specific menu or screen.
 * Emulates legacy 3270 screen flows (Enter, PF-Keys, etc.).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        // menuId and action validity enforced in the aggregate
    }
}