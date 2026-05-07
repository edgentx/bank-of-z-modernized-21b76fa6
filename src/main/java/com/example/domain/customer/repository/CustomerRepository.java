package com.example.domain.customer.repository;

import com.example.domain.customer.model.CustomerAggregate;

public interface CustomerRepository {
    CustomerAggregate save(CustomerAggregate aggregate);
    CustomerAggregate findById(String id);
}
