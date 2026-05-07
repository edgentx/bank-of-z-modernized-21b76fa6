package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartTellerSessionCmd(String sessionId, String tellerId, String branchId) implements Command {}
