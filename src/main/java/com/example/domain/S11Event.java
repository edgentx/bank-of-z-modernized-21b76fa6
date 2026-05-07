package com.example.domain;

public sealed interface S11Event permits S11Event.WithdrawalPosted {

    record WithdrawalPosted(
        String accountNumber,
        java.math.BigDecimal amount,
        String currency
    ) implements S11Event {}
}
