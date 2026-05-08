package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate between screens in the teller UI.
 */
public record NavigateMenuCmd(String sessionId, String currentContext, String targetMenuId, String action) implements Command {}
