package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command for a teller to navigate to a specific menu or screen.
 * Used to emulate legacy 3270 menu navigation flows.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
