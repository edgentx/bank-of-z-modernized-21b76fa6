package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
