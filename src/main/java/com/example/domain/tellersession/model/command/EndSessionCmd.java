package com.example.domain.tellersession.model.command;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
