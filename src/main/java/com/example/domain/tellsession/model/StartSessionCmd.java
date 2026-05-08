package com.example.domain.tellsession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
