package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Context: S-20 (User Interface Navigation).
 */
public record EndSessionCmd(String sessionId) implements Command {}
