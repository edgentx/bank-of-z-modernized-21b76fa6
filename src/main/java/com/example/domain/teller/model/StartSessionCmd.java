package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * @param tellerId The ID of the authenticated teller.
 * @param terminalId The ID of the physical or virtual terminal being used.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
