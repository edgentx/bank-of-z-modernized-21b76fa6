package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Emulates legacy menu navigation (e.g., F3=Back, Enter=Submit).
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String requiredContext // Used to enforce operational context invariants
) implements Command {}