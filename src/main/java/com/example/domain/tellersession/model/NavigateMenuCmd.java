package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Emulates legacy 3270 AID (Attention Identifier) keys like Enter, PF3, etc.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action  // e.g., "ENTER", "PF3" (Exit), "PF12" (Cancel)
) implements Command {}
