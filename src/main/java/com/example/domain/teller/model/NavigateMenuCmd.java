package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for a teller to navigate to a specific menu or screen.
 * Part of user-interface-navigation (S-19).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
