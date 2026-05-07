package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for navigating the Teller UI to a specific menu.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
