package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {}
