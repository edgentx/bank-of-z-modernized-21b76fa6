package com.example.domain.tellerauth.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Used for legacy menu navigation emulation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
