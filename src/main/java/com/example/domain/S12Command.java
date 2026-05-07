package com.example.domain;

import java.util.Objects;
import java.util.UUID;

public class S12Command {
    private final UUID originalTransactionId;

    public S12Command(TransactionId originalTransactionId) {
        this.originalTransactionId = originalTransactionId.value();
    }

    public UUID getOriginalTransactionId() {
        return originalTransactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        S12Command that = (S12Command) o;
        return Objects.equals(originalTransactionId, that.originalTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalTransactionId);
    }
}
