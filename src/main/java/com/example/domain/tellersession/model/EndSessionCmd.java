package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to terminate a teller session and clear sensitive state.
 * Part of S-20: TellerSession user-interface-navigation.
 */
public record EndSessionCmd(UUID sessionId) implements Command {}
