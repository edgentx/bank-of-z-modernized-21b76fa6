package com.example.domain.tellermessaging.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.UUID;

public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action,
    Instant timestamp
) implements Command {}
