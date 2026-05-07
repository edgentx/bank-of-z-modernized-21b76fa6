package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
  Optional<TellerSessionAggregate> findById(String sessionId);
  void save(TellerSessionAggregate aggregate);
}
