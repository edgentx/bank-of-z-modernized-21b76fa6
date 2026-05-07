package com.example.domain;

import com.example.domain.shared.Command;

/**
 * Sample Command for the S-1 BDD scenarios.
 */
public record S1Command(String action, String payload) implements Command {}
