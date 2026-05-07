package com.vforce360.transaction.ports;

import java.math.BigDecimal;

public interface AccountStatePort {
    BigDecimal getBalance(String accountNumber);
}
