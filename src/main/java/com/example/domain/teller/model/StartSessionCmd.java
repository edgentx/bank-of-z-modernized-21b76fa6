package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Internal helper command to establish session state for testing context.
 * In a real flow, this would be handled by a 'StartSession' story.
 */
public record StartSessionCmd(String sessionId, String tellerId, String initialScreen) implements Command {}
