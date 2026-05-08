package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu or screen.
 * Corresponds to Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
