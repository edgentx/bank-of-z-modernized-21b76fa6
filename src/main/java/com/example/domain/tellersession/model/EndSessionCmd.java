package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 */
public record EndSessionCmd() implements Command {
}