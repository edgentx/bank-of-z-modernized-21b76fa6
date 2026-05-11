package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 */
public record NavigateMenuCmd(
    String sessionId,
    String currentMenuId, // Used to verify context
    String menuId,        // Target menu
    String action         // Action to take (e.g., ENTER, PF3)
) implements Command {}