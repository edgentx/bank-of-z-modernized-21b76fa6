package com.example.domain;

import java.util.Objects;

public record AccountNumber(String value) {
    public AccountNumber {
        Objects.requireNonNull(value);
        if (value.isBlank()) throw new IllegalArgumentException("Account number cannot be blank");
    }

    public static AccountNumber of(String number) {
        return new AccountNumber(number);
    }
}
