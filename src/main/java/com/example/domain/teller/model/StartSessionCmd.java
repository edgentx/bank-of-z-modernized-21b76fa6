package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to initiate a new Teller Session.
 * (Record implementation for immutable data carrier).
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    String authToken,
    String sessionId
) implements Command {}