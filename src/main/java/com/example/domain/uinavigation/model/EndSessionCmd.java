package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
