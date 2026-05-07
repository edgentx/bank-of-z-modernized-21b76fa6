package com.example.domain;

import java.util.UUID;

// Using standard interface instead of sealed class to ensure compilation compatibility
// and avoid strict hierarchical constraints that were causing the build failure.
public interface DomainEvent {
    UUID eventId();
    long timestamp();
}