package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu or screen.
 * Part of the S-19 story: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(String targetMenuId, String action) implements Command {

    public NavigateMenuCmd {
        if (targetMenuId == null || targetMenuId.isBlank()) {
            throw new IllegalArgumentException("targetMenuId cannot be blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be blank");
        }
    }
}