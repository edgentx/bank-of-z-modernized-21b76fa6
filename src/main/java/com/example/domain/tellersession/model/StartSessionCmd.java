package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Context: S-18 User Interface Navigation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}