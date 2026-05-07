package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record NavigateMenuCmd(String sessionId, String menuId, String action, Instant occurredAt) implements Command {
}