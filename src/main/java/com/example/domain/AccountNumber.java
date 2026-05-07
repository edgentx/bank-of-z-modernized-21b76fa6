package com.example.domain;

public record AccountNumber(String value) {
    public AccountNumber {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("AccountNumber cannot be blank");
    }
}
