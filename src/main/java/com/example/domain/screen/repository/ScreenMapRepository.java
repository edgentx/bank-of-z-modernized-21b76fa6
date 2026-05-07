package com.example.domain.screen.repository;

import com.example.domain.screen.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
  Optional<ScreenMapAggregate> findById(String screenMapId);
  void save(ScreenMapAggregate aggregate);
}
