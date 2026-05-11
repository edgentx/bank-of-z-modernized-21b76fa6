package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
    // In-memory test helpers often have a deleteAll, but strict domain repos might not.
    // Included for InMemoryRepo convenience typically.
}