package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {}
