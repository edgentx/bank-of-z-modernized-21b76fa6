package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}