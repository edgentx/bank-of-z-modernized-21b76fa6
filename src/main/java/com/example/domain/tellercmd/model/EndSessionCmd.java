package com.example.domain.tellercmd.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
