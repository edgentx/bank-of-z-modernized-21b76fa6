package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Follows the Repository pattern strictness.
 */
public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
    // Other necessary CRUD methods can be defined here as needed.
}
