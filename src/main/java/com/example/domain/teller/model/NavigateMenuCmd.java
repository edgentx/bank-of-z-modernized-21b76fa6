package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu.
 * Part of Story S-19: user-interface-navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
