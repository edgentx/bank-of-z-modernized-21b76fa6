package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Internal command to establish the authenticated state required by invariants.
 */
public record TellerLoggedInCmd(String tellerId) implements Command {}
