package com.example.domain.customer.model;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
public class CustomerAggregate extends AggregateRoot {
  private String customerId;
  private String fullName;
  private String email;
  private boolean enrolled;
  public CustomerAggregate(String customerId) { this.customerId = customerId; }
  @Override public String id() { return customerId; }
  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof EnrollCustomerCmd c) {
      if (enrolled) throw new IllegalStateException("Customer already enrolled: " + c.customerId());
      if (c.fullName() == null || c.fullName().isBlank()) throw new IllegalArgumentException("fullName required");
      if (c.email() == null || !c.email().contains("@")) throw new IllegalArgumentException("valid email required");
      if (c.governmentId() == null || c.governmentId().isBlank()) throw new IllegalArgumentException("governmentId required");
      var event = new CustomerEnrolledEvent(c.customerId(), c.fullName(), c.email(), c.governmentId(), Instant.now());
      this.fullName = c.fullName();
      this.email = c.email();
      this.enrolled = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }
  public boolean isEnrolled() { return enrolled; }
  public String getFullName() { return fullName; }
  public String getEmail() { return email; }
}
