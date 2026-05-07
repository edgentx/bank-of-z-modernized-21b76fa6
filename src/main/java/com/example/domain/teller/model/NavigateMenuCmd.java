package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal user interface to a new menu or screen.
 * Encapsulates the legacy 3270 data stream navigation intent.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
