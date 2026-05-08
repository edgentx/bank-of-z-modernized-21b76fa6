package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartSessionCmd(String tellerId, String terminalId, Instant authenticatedAt, String currentContext) implements Command {
}