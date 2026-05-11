package com.example.domain.customer;

import com.example.domain.customer.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerAggregateTest {

    @Test
    void testEnrollCustomerSuccess() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        EnrollCustomerCmd cmd = new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV-123");

        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof CustomerEnrolledEvent);
        assertTrue(aggregate.isEnrolled());
        assertEquals("John Doe", aggregate.getFullName());
    }

    @Test
    void testEnrollCustomerThrowsOnInvalidEmail() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        EnrollCustomerCmd cmd = new EnrollCustomerCmd("cust-1", "John Doe", "invalid-email", "GOV-123");

        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    // --- S-3 Tests: UpdateCustomerDetailsCmd ---

    @Test
    void testUpdateCustomerDetailsSuccess() {
        // Setup: Enrolled customer
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "Old Name", "old@example.com", "GOV-123"));
        aggregate.clearEvents(); // Clear enrollment events

        // Execute Update Command
        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("cust-1", "New Name", "new@example.com", "SORT-01");
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assertions
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof CustomerDetailsUpdatedEvent);
        
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) events.get(0);
        assertEquals("cust-1", event.customerId());
        assertEquals("New Name", event.fullName());
        assertEquals("new@example.com", event.emailAddress());
        assertEquals("SORT-01", event.sortCode());
        assertEquals("customer.details.updated", event.type());

        // Verify Aggregate State Updated
        assertEquals("New Name", aggregate.getFullName());
        assertEquals("new@example.com", aggregate.getEmail());
    }

    @Test
    void testUpdateDetailsRejectedIfEmptyName() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John", "john@example.com", "GOV-123"));
        aggregate.clearEvents();

        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("cust-1", "", "john@example.com", "SORT-01");
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("fullName cannot be empty"));
    }

    @Test
    void testUpdateDetailsRejectedIfInvalidEmail() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John", "john@example.com", "GOV-123"));
        aggregate.clearEvents();

        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("cust-1", "John", "invalid-email", "SORT-01");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("valid email required"));
    }

    @Test
    void testUpdateDetailsRejectedIfEmptySortCode() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John", "john@example.com", "GOV-123"));
        aggregate.clearEvents();

        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("cust-1", "John", "john@example.com", "");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("sortCode cannot be empty"));
    }

    @Test
    void testUpdateDetailsFailsIfCustomerNotEnrolled() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1"); // Not enrolled

        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("cust-1", "John", "john@example.com", "SORT-01");

        Exception ex = assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("not enrolled"));
    }

    // --- S-3 Tests: DeleteCustomerCmd ---

    @Test
    void testDeleteCustomerSuccess() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John", "john@example.com", "GOV-123"));
        aggregate.clearEvents();

        DeleteCustomerCmd cmd = new DeleteCustomerCmd("cust-1", false); // No active accounts
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof CustomerDeletedEvent);
        assertTrue(aggregate.isDeleted());
    }

    @Test
    void testDeleteCustomerRejectedIfActiveAccounts() {
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John", "john@example.com", "GOV-123"));
        aggregate.clearEvents();

        DeleteCustomerCmd cmd = new DeleteCustomerCmd("cust-1", true); // Has active accounts

        Exception ex = assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("Cannot delete customer with active accounts"));
    }
}
