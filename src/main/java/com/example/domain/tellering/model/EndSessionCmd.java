package com.example.domain.tellering.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
