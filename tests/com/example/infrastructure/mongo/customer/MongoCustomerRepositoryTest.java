package com.example.infrastructure.mongo.customer;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoCustomerRepositoryTest {

  private final CustomerMongoDataRepository data = mock(CustomerMongoDataRepository.class);
  private final MongoCustomerRepository repo = new MongoCustomerRepository(data);

  @Test
  void saveMapsEveryFieldOntoDocument() {
    CustomerAggregate agg = new CustomerAggregate("cust-1");
    agg.execute(new EnrollCustomerCmd("cust-1", "Jane Doe", "jane@example.com", "GOV123"));
    agg.execute(new UpdateCustomerDetailsCmd("cust-1", "jane@example.com", "12-34-56"));

    repo.save(agg);

    var doc = repo.toDocument(agg);
    assertEquals("cust-1", doc.getId());
    assertEquals("Jane Doe", doc.getFullName());
    assertEquals("jane@example.com", doc.getEmail());
    assertEquals("12-34-56", doc.getSortCode());
    assertTrue(doc.isEnrolled());
    assertEquals(2, doc.getVersion());
    verify(data).save(any(CustomerDocument.class));
  }

  @Test
  void findByIdRestoresAggregateState() {
    var doc = new CustomerDocument("cust-2", "Alice", "alice@example.com", "00-11-22", true, 3);
    when(data.findById("cust-2")).thenReturn(Optional.of(doc));

    CustomerAggregate restored = repo.findById("cust-2").orElseThrow();

    assertEquals("cust-2", restored.id());
    assertEquals("Alice", restored.getFullName());
    assertEquals("alice@example.com", restored.getEmail());
    assertEquals("00-11-22", restored.getSortCode());
    assertTrue(restored.isEnrolled());
    assertEquals(3, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("nope")).thenReturn(Optional.empty());
    assertTrue(repo.findById("nope").isEmpty());
  }
}
