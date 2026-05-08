package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for S-19: NavigateMenuCmd.
 * Routes the teller to a different menu or screen.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
