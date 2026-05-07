package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to navigate the Teller UI to a specific menu/screen.
 * Part of S-19: TellerSession user-interface-navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null/blank");
        }
        if (menuId == null || menuId.isBlank()) {
            throw new IllegalArgumentException("menuId cannot be null/blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be null/blank");
        }
    }
}