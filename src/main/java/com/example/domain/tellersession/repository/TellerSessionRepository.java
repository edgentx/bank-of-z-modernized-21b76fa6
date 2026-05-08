package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregate.
 * Note: Following the pattern established by Customer/Transaction repositories.
 */
public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    TellerSession load(String id);
    Optional<TellerSession> findById(String id);
}