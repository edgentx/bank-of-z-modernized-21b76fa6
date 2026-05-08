package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String screenId);
    void save(ScreenMapAggregate aggregate);
}
