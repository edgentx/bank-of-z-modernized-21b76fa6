package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to route the teller to a different menu or screen.
 * Part of Story S-19: TellerSession Navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {

    public NavigateMenuCmd {
        // Basic validation at the command boundary
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
