package com.example.mocks;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.repository.CustomerRepository;
import java.util.*;
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, CustomerAggregate> store = new HashMap<>();
    @Override public void save(CustomerAggregate a) { store.put(a.id(), a); }
    @Override public Optional<CustomerAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}