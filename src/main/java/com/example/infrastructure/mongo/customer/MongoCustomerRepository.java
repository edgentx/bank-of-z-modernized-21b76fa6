package com.example.infrastructure.mongo.customer;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.repository.CustomerRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * MongoDB adapter implementing the {@link CustomerRepository} domain port.
 *
 * Maps {@link CustomerAggregate} ↔ {@link CustomerDocument}. The aggregate's
 * private fields are accessed reflectively (see {@link AggregateFieldAccess})
 * so the domain class stays untouched by persistence concerns.
 */
@Component
public class MongoCustomerRepository implements CustomerRepository {

  private final CustomerMongoDataRepository data;

  public MongoCustomerRepository(CustomerMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<CustomerAggregate> findById(String customerId) {
    return data.findById(customerId).map(this::toAggregate);
  }

  @Override
  public void save(CustomerAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  CustomerDocument toDocument(CustomerAggregate agg) {
    return new CustomerDocument(
        agg.id(),
        agg.getFullName(),
        agg.getEmail(),
        agg.getSortCode(),
        agg.isEnrolled(),
        agg.getVersion());
  }

  CustomerAggregate toAggregate(CustomerDocument doc) {
    CustomerAggregate agg = new CustomerAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "fullName", doc.getFullName());
    AggregateFieldAccess.set(agg, "email", doc.getEmail());
    AggregateFieldAccess.set(agg, "sortCode", doc.getSortCode());
    AggregateFieldAccess.set(agg, "enrolled", doc.isEnrolled());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
