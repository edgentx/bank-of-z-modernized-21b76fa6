package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Domain port for TellerSessionAggregate persistence.
 * S-28 adds the MongoDB adapter; existing test mocks may implement this.
 */
public interface TellerSessionRepository {
  Optional<TellerSessionAggregate> findById(String sessionId);
  void save(TellerSessionAggregate aggregate);
}
