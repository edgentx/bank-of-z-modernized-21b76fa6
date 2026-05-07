package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen based on input.
 * Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
