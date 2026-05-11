package com.example.domain.userinterface.repository;

import com.example.domain.shared.Aggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    Aggregate findById(String id);
    void save(Aggregate aggregate);
}
