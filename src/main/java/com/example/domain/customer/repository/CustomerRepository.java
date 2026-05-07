package com.example.domain.customer.repository;
import com.example.domain.customer.model.CustomerAggregate;
import java.util.Optional;
public interface CustomerRepository {
  Optional<CustomerAggregate> findById(String customerId);
  void save(CustomerAggregate aggregate);
}
