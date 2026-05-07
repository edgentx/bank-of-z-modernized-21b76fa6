package com.vforce360.transaction.ports;

import java.math.BigDecimal;

/**
 * Port interface for Account operations.
 * Abstracts the underlying system (Mainframe/CICS/Mongo) from the Domain.
 */
public interface AccountPort {
    /**
     * Retrieves the current balance for the account.
     * @param accountNumber The unique identifier.
     * @return The current balance.
     */
    BigDecimal getBalance(String accountNumber);
}
