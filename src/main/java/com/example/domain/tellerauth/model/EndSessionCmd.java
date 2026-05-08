package com.example.domain.tellerauth.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
