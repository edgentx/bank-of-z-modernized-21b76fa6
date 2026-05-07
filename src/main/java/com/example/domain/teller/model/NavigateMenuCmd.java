package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal UI to a specific menu or screen.
 * Emulates legacy 3270 menu navigation flows.
 */
public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action
) implements Command {}
