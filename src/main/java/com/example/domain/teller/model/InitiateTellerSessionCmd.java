package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record InitiateTellerSessionCmd(String sessionId, String tellerId) implements Command {}
