package com.example.domain.tellercmd;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {}
