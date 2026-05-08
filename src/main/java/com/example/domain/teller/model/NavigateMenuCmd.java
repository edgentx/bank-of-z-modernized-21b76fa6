package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for S-19: Navigate Menu.
 * Records the teller's intent to move to a specific UI element.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}
