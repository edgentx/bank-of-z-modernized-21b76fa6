package com.example.domain.customer.model;

import com.example.domain.shared.Command;

public record UpdateCustomerDetailsCmd(String customerId, String fullName, String emailAddress, String sortCode) implements Command {
    // Renaming emailAddress to email in constructor params to match internal standard usage, 
    // but keeping accessor logic consistent if needed.
    // Actually, keeping it simple: the record fields are the contract.
    public String email() { return emailAddress; }
}
