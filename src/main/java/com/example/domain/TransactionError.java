package com.example.domain;

public class TransactionError extends RuntimeException {
    public TransactionError(String message) {
        super(message);
    }
}