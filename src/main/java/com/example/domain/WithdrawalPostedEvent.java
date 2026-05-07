package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record WithdrawalPostedEvent(String accountNumber, BigDecimal amount, Currency currency, BigDecimal balanceAfter) implements DomainEvent {
}