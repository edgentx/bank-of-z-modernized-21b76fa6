package com.example.domain.session.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 */
public record EndSessionCmd() implements Command {
}
