package com.example.domain.tellerrsession.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
