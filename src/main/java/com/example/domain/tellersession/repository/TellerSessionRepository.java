package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

import java.util.Optional;

/**
 * Repository interface for the TellerSession aggregate.
 * Fixed to import the correct Aggregate type.
 */
public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
