package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to close an account.
 * The accountId must match the aggregate ID.
 */
public record CloseAccountCmd(String accountId) implements Command {}
