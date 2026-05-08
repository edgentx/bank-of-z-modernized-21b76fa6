package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command for a teller to navigate to a specific menu screen.
 * Part of User Interface Navigation (S-19).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
