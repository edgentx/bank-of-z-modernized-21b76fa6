package com.example.domain;

/**
 * Command to navigate to a specific screen in the teller UI.
 * Code generated for S-19.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) {}
