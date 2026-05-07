package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu or screen.
 * Used in S-19: TellerSession Menu Navigation.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {}