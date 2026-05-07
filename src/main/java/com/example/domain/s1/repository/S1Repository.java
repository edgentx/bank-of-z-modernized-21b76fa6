package com.example.domain.s1.repository;

import com.example.domain.s1.model.S1Aggregate;
import java.util.Optional;

public interface S1Repository {
    void save(S1Aggregate aggregate);
    Optional<S1Aggregate> findById(String id);
}