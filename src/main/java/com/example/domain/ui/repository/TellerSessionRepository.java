package com.example.domain.ui.repository;

import com.example.domain.ui.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSessionAggregate.
 * S-18: Implement StartSessionCmd on TellerSession.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    TellerSessionAggregate create(String id);
}
