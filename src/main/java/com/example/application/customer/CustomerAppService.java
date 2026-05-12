package com.example.application.customer;

import com.example.application.AggregateNotFoundException;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

/**
 * Customer-management application service.
 *
 * Loads the aggregate from its port, applies the command via the canonical
 * {@code execute(Command)} entry, and persists the new state. Domain
 * exceptions (IllegalArgumentException, IllegalStateException) propagate to
 * the global exception handler.
 */
@Service
public class CustomerAppService {

  private final CustomerRepository repository;

  public CustomerAppService(CustomerRepository repository) {
    this.repository = repository;
  }

  public CustomerAggregate enroll(EnrollCustomerCmd cmd) {
    CustomerAggregate aggregate = repository
        .findById(cmd.customerId())
        .orElseGet(() -> new CustomerAggregate(cmd.customerId()));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public CustomerAggregate updateDetails(UpdateCustomerDetailsCmd cmd) {
    CustomerAggregate aggregate = repository
        .findById(cmd.customerId())
        .orElseThrow(() -> new AggregateNotFoundException("Customer", cmd.customerId()));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public void delete(DeleteCustomerCmd cmd) {
    CustomerAggregate aggregate = repository
        .findById(cmd.customerId())
        .orElseThrow(() -> new AggregateNotFoundException("Customer", cmd.customerId()));
    aggregate.execute(cmd);
    repository.save(aggregate);
  }

  public CustomerAggregate findById(String customerId) {
    return repository
        .findById(customerId)
        .orElseThrow(() -> new AggregateNotFoundException("Customer", customerId));
  }
}
