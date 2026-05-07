package com.example.domain.tellersession.command;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
