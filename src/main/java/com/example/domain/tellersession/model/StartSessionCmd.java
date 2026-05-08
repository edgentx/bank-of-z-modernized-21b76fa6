package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Duration;

/**
 * Command to initiate a teller session.
 * AC: S-18
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String sourceChannelId,
    String currentContext,
    Duration timeout
) implements Command {}