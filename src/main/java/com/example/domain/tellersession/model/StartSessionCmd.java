package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {}
