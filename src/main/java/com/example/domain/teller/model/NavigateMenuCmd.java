package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for S-19.
 * Routes the teller to a different menu or screen based on input.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
