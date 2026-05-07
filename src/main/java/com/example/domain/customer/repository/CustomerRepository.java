package com.example.domain.customer.repository;

import com.example.domain.customer.model.CustomerAggregate;

import java.util.Optional;

public interface CustomerRepository {
    void save(CustomerAggregate aggregate);
    Optional<CustomerAggregate> findById(String id);
}