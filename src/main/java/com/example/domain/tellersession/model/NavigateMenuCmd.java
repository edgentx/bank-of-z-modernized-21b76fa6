package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Emulates legacy menu navigation behavior (S-19).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    // Validation is handled by the aggregate to enforce domain invariants
}