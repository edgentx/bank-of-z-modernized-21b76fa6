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
  private String sortCode;
  private boolean enrolled;
  private boolean nameDobViolation;
  private boolean activeAccountsViolation;
  public CustomerAggregate(String customerId) { this.customerId = customerId; }
  @Override public String id() { return customerId; }

  /** Test seam: mark this aggregate as violating the name/date-of-birth invariant. */
  public void markNameDobViolation() { this.nameDobViolation = true; }
  /** Test seam: mark this aggregate as having active bank accounts (delete-protection invariant). */
  public void markActiveAccountsViolation() { this.activeAccountsViolation = true; }

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
    if (cmd instanceof DeleteCustomerCmd c) {
      // S-4 declared the DeleteCustomerCmd + CustomerDeletedEvent types but
      // the handler was never wired into execute(), so every scenario routed
      // through UnknownCommandException. Same three invariants as
      // UpdateCustomerDetailsCmd, plus the application-supplied
      // hasActiveAccounts flag.
      if (!enrolled) {
        throw new IllegalStateException("A customer must have a valid, unique email address and government-issued ID.");
      }
      if (nameDobViolation || fullName == null || fullName.isBlank()) {
        throw new IllegalStateException("Customer name and date of birth cannot be empty.");
      }
      if (activeAccountsViolation || c.hasActiveAccounts()) {
        throw new IllegalStateException("A customer cannot be deleted if they own active bank accounts.");
      }
      var event = new CustomerDeletedEvent(c.customerId(), Instant.now());
      this.enrolled = false;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    if (cmd instanceof UpdateCustomerDetailsCmd c) {
      // Invariant: A customer must have a valid, unique email address and government-issued ID.
      if (!enrolled) {
        throw new IllegalStateException("A customer must have a valid, unique email address and government-issued ID.");
      }
      if (c.emailAddress() == null || !c.emailAddress().contains("@")) {
        throw new IllegalArgumentException("A customer must have a valid, unique email address and government-issued ID.");
      }
      // Invariant: Customer name and date of birth cannot be empty.
      if (nameDobViolation || fullName == null || fullName.isBlank()) {
        throw new IllegalStateException("Customer name and date of birth cannot be empty.");
      }
      // Invariant: A customer cannot be deleted if they own active bank accounts.
      if (activeAccountsViolation) {
        throw new IllegalStateException("A customer cannot be deleted if they own active bank accounts.");
      }
      if (c.sortCode() == null || c.sortCode().isBlank()) {
        throw new IllegalArgumentException("sortCode required");
      }
      var event = new CustomerDetailsUpdatedEvent(c.customerId(), c.emailAddress(), c.sortCode(), Instant.now());
      this.email = c.emailAddress();
      this.sortCode = c.sortCode();
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }
  public boolean isEnrolled() { return enrolled; }
  public String getFullName() { return fullName; }
  public String getEmail() { return email; }
  public String getSortCode() { return sortCode; }
}
