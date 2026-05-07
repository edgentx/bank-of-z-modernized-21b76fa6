package com.example.domain;

import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Transaction findById(UUID id);
}
