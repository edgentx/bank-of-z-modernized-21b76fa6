package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a Teller Session.
 * Validated against invariants:
 * 1. Authentication check (tellerId must not be null/blank if session is active).
 * 2. Timeout check (session must not be expired).
 * 3. Navigation state check (state must be consistent).
 */
public record EndSessionCmd(String sessionId) implements Command {}
