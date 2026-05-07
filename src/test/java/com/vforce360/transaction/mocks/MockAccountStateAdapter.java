package com.vforce360.transaction.mocks;

import com.vforce360.transaction.ports.AccountStatePort;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MockAccountStateAdapter implements AccountStatePort {
    private final Map<String, BigDecimal> balances = new HashMap<>();

    public void setBalance(String accountNumber, BigDecimal balance) {
        balances.put(accountNumber, balance);
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        return balances.getOrDefault(accountNumber, BigDecimal.ZERO);
    }
}
