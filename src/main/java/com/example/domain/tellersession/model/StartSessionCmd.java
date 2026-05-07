package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, String authToken, String navigationState) implements Command {}
