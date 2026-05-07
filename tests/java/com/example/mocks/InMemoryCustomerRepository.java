package com.example.mocks;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.repository.CustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, CustomerAggregate> store = new HashMap<>();

    @Override
    public void save(CustomerAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<CustomerAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
