package com.example.domain.aggregator.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
