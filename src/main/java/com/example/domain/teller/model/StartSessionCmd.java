package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Part of S-18 User Interface Navigation.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
