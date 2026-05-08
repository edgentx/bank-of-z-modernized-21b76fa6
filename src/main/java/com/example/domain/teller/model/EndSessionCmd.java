package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to terminate a Teller Session.
 * Context: BANK S-20 — User Interface Navigation.
 */
public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {}
