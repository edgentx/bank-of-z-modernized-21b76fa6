package com.example.domain.tellsession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or screen.
 * Part of Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
