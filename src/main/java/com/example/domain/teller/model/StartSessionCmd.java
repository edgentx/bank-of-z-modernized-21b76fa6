package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Maps to the S-18 User Interface Navigation story.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
