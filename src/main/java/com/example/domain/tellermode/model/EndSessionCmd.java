package com.example.domain.tellermode.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {
}
