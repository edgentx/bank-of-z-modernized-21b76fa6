package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

public record EndSessionCmd(UUID sessionId) implements Command {}
