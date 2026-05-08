package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal UI to a specific menu or screen.
 * Part of the legacy 3270 emulation layer.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String authContext // e.g., authenticated teller ID
) implements Command {}
