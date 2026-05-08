package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the Teller UI to a specific menu context.
 * Used to emulate legacy 3270 menu traversal or modern React route changes.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        // menuId and action can be context-dependent, so we allow them to be flexible
        // but typically should not be null.
        if (menuId == null) throw new IllegalArgumentException("menuId cannot be null");
        if (action == null) throw new IllegalArgumentException("action cannot be null");
    }
}
