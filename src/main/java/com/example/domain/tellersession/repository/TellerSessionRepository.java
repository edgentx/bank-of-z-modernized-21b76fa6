package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
  void save(TellerSessionAggregate aggregate);
  Optional<TellerSessionAggregate> findById(String id);
}
