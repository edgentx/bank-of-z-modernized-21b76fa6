package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Internal command used to setup the authenticated state for the aggregate
 * in the context of the BDD scenarios.
 */
public record AuthenticateTellerCmd(String tellerId) implements Command {}
