package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
