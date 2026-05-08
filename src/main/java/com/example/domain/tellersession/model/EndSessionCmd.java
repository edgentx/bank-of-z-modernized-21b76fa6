package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to end a teller session.
 */
public record EndSessionCmd(UUID sessionId, String tellerId) implements Command {
}
