package com.example.ports;

/**
 * Port interface for interacting with the persistence layer (DB2/MongoDB).
 * This decouples the domain logic from specific database implementations.
 */
public interface JpaAuditLogPort {
    void log(String message);
}
