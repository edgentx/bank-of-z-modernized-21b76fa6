package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record EndTellerSessionCmd(String sessionId) implements Command {}
