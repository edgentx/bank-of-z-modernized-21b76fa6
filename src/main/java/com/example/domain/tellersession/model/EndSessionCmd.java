package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record EndSessionCmd(String sessionId, Instant timestamp) implements Command {}
