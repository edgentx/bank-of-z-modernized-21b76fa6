package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record InitiateSessionCmd(String sessionId, String tellerId) implements Command {}
