package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * 
 * @param tellerId    The ID of the authenticated teller.
 * @param terminalId  The ID of the terminal where the session is starting.
 * @param authenticated Whether the teller has passed primary authentication checks.
 */
public record StartSessionCmd(String tellerId, String terminalId, boolean authenticated) implements Command {}
