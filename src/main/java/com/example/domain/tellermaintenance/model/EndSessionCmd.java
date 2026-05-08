package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
