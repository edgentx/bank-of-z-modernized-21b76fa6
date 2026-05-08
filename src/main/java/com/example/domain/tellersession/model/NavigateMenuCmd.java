package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or screen.
 * @param sessionId The unique identifier for the teller session.
 * @param targetMenuId The destination menu identifier (e.g., "MAIN_MENU", "DEPOSIT_SCREEN").
 * @param action The action taken to trigger navigation (e.g., "ENTER", "PF3").
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {}
