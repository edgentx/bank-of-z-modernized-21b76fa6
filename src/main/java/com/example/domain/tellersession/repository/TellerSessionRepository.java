package com.example.domain.tellersession.repository;

import com.example.domain.teller.model.TellerSession;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregate.
 * Note: Package tellsession.repository aligns with error logs requiring TellerSessionAggregate logic to be resolved here.
 */
public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
