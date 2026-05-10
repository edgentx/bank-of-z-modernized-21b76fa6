package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Part of Story S-19: TellerSession User Interface Navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
