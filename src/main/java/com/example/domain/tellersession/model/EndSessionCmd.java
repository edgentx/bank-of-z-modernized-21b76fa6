package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current Teller Session.
 */
public record EndSessionCmd(String sessionId) implements Command {}
