package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to create a new session. Included to support the 'valid aggregate' Given steps.
 */
public record InitiateSessionCmd(String sessionId, String tellerId) implements Command {
}
