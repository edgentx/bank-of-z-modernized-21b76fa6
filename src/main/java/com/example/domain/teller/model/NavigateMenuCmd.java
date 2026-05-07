package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal menu system.
 * Corresponds to Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}