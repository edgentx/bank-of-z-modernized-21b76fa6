package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Mirrors legacy 3270 navigation behavior within the Aggregate.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
