package com.example.domain.validation;

import java.time.Instant;

public class Validation {
    private String id;
    private String status;
    private Instant createdAt;

    public Validation(String id, String status, Instant createdAt) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
