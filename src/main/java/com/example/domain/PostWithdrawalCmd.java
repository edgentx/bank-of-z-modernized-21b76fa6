package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record PostWithdrawalCmd(String accountNumber, BigDecimal amount, Currency currency) {
}