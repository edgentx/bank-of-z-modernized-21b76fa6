package com.example.adapters;

import com.example.ports.JpaAuditLogPort;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the JpaAuditLogPort.
 * Writes logs to the actual database infrastructure.
 */
@Component
public class JpaAuditLogAdapter implements JpaAuditLogPort {

    @Override
    public void log(String message) {
        // Implementation would write to DB2 or MongoDB.
        // Stubbed for compilation purposes.
    }
}
