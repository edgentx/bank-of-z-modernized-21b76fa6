package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * S-20: Implement EndSessionCmd on TellerSession
 */
public interface TellerSessionRepository {
    void save(TellerSession session);
    Optional<TellerSession> findById(String id);
}
