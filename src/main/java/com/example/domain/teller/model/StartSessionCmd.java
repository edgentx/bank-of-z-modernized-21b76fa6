package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 * 
 * @param sessionId   The unique identifier for the session aggregate.
 * @param tellerId    The ID of the teller initiating the session.
 * @param terminalId  The ID of the terminal being used.
 * @param authenticated boolean flag indicating if the teller has passed auth checks.
 * @param occurredAt  The time the command was issued (used for timeout validation).
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated,
        Instant occurredAt
) implements Command {}
