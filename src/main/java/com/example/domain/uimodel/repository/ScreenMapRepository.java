package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}