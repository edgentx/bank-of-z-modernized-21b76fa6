package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * Used to establish the authenticated state before navigation can occur.
 */
public record InitiateTellerSessionCmd(String sessionId, String tellerId, Instant initiatedAt) implements Command {}
