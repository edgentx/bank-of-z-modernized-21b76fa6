package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Part of the User-Interface-Navigation bounded context.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
