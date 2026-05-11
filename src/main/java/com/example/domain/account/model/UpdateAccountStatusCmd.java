package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to update the status of an Account.
 */
public record UpdateAccountStatusCmd(String accountNumber, String newStatus) implements Command {
}
